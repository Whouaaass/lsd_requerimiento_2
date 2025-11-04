package audioplayer

import (
	"context"
	"fmt"
	"io"
	"time"

	"github.com/faiface/beep"
	"github.com/faiface/beep/mp3"
	"github.com/faiface/beep/speaker"
)

// ... (tu struct `pipe` y sus métodos no cambian) ...
type pipe struct {
	ch     <-chan []byte
	buffer []byte
}

func (p *pipe) Read(b []byte) (n int, err error) {
	if len(p.buffer) > 0 {
		n = copy(b, p.buffer)
		p.buffer = p.buffer[n:]
		return n, nil
	}
	data, ok := <-p.ch
	if !ok {
		return 0, io.EOF
	}
	n = copy(b, data)
	if n < len(data) {
		p.buffer = data[n:]
	}
	return n, nil
}

func (p *pipe) Close() error {
	return nil
}

func PlayStream(ctx context.Context, audioChunksChan <-chan []byte, statusChan chan<- string) error {
	// 1. Decodificar el stream
	audioPipe := &pipe{ch: audioChunksChan}
	streamer, format, err := mp3.Decode(audioPipe)
	if err != nil {
		statusChan <- fmt.Sprintf("❌ Error de decodificación: %v", err)
		return fmt.Errorf("error decoding stream: %w", err)
	}
	defer streamer.Close()

	// 2. Inicializar el altavoz (una sola vez es ideal, pero aquí está bien)
	// NOTA: Si llamas a PlayStream varias veces, considera inicializar el speaker una sola vez al inicio de tu app.
	if err := speaker.Init(format.SampleRate, format.SampleRate.N(time.Second/10)); err != nil {
		statusChan <- fmt.Sprintf("❌ Error al inicializar altavoz: %v", err)
		return fmt.Errorf("error initializing speaker: %w", err)
	}
	defer speaker.Close()

	statusChan <- "🔊 Reproducción iniciada."

	// 3. Preparar el control y la señal de finalización
	done := make(chan struct{})
	ctrl := &beep.Ctrl{Streamer: beep.Seq(streamer, beep.Callback(func() {
		// Esta función se llamará cuando el streamer termine, cerrando el canal 'done'.
		close(done)
	}))}

	// 4. Iniciar la reproducción
	// `speaker.Play` es seguro para ser llamado concurrentemente. Añade nuestro `ctrl` a la cola.
	speaker.Play(ctrl)

	// 5. 💡 **LÓGICA CORREGIDA: Esperar el resultado final con un solo 'select'**
	// La función se bloqueará aquí hasta que el contexto se cancele o la canción termine.
	select {
	case <-ctx.Done():
		// El contexto fue cancelado. Pausamos de forma segura.
		statusChan <- "⏹️ Deteniendo reproducción por petición..."
		speaker.Lock()
		ctrl.Paused = true
		speaker.Unlock()
		statusChan <- "[audioplayer] Reproductor pausado."
	case <-done:
		// La canción terminó por sí sola.
		statusChan <- "🎶 La canción ha terminado."
	}

	// Pequeña espera opcional para asegurar que el speaker procese la pausa
	time.Sleep(100 * time.Millisecond)
	return nil
}

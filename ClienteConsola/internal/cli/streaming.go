package cli

import (
	"fmt"
	"sync"

	"musis.cliente/grpc-cliente/pkg/audioplayer"
	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

// runStreamingTask is the main function for handling audio streaming.
// It runs in a separate goroutine and communicates with the model
// via channels.
func (m *model) runStreamingTask() {
	// This function uses the model's context and channels:
	// m.ctx, m.statusChan, m.errorChan, m.doneChan

	if m.selectedCancion == nil {
		return
	}

	var wg sync.WaitGroup
	// Use the model's context, which can be cancelled by pressing 's'
	ctx := m.ctx

	audioChunksChan := make(chan []byte, 100)

	// We use internal channels here to log status/errors
	// and then forward them to the model's channels.
	internalStatusChan := make(chan string, 10)
	internalErrorChan := make(chan error, 10)

	// Start goroutines to forward internal messages to the model
	wg.Add(2)
	go func() {
		defer wg.Done()
		for {
			select {
			case <-ctx.Done():
				return
			case status, ok := <-internalStatusChan:
				if !ok {
					return
				}
				// Send to model
				m.statusChan <- status
			}
		}
	}()
	go func() {
		defer wg.Done()
		for {
			select {
			case <-ctx.Done():
				return
			case err, ok := <-internalErrorChan:
				if !ok {
					return
				}
				// Send to model
				m.errorChan <- err
			}
		}
	}()

	// Iniciar el streaming en segundo plano
	wg.Add(1)
	go func() {
		defer wg.Done()
		m.audioStreamService.StreamAudioOfSong(&pb.PeticionStreamDTO{
			IdUsuario: m.user.Id,
			Cancion: &pb.CancionDTO{
				Id:                 m.selectedCancion.Id,
				Titulo:             m.selectedCancion.Titulo,
				Autor:              m.selectedCancion.Artista,
				Album:              "",
				Genero:             m.selectedCancion.Genero,
				Idioma:             m.selectedCancion.Idioma,
				AnioLanzamiento:    0,
				DuracionS:          0,
				RutaAlmacenamiento: m.selectedCancion.RutaAlmacenamiento,
			},
		}, ctx, audioChunksChan, internalStatusChan)
	}()

	// Iniciar el reproductor en segundo plano
	wg.Add(1)
	go func() {
		defer wg.Done()
		err := audioplayer.PlayStream(ctx, audioChunksChan, internalStatusChan)
		if err != nil {
			internalErrorChan <- fmt.Errorf("audioplayer error: %w", err)
		}
	}()

	// Wait for all goroutines (streaming, player, log forwarders) to finish.
	wg.Wait()

	// Clean up internal channels
	close(internalErrorChan)
	close(internalStatusChan)

	// Signal the model that playback is complete.
	// We check for nil in case it was already closed by a stop/quit action.
	if m.doneChan != nil {
		m.doneChan <- struct{}{}
	}
}

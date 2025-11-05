package streamingService

import (
	"context"
	"fmt"
	"io"

	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

type ProcedimientosStreaming struct {
	client pb.AudioServiceClient
}

func New(client pb.AudioServiceClient) *ProcedimientosStreaming {
	return &ProcedimientosStreaming{
		client: client,
	}
}

// StreamAudio se conecta al servidor gRPC y retransmite los fragmentos de audio.
// No tiene conocimiento de la vista ni del reproductor de audio.
func (pr *ProcedimientosStreaming) StreamAudio(songId int32, ctx context.Context, audioChunksChan chan<- []byte, statusChan chan<- string) {
	// Es responsabilidad de esta función cerrar el canal de audio cuando termine.
	defer close(audioChunksChan)

	client := pr.client

	// Petición de ejemplo
	request := &pb.PeticionDTO{Id: songId}

	// Llamada al método de streaming
	stream, err := client.EnviarCancionMedianteStream(ctx, request)
	if err != nil {
		statusChan <- fmt.Sprintf("❌ Error al iniciar el stream: %v", err)
		return
	}

	statusChan <- "🎧 Streaming iniciado. Recibiendo datos..."

	for {
		// Recibir un fragmento de la canción
		fragmento, err := stream.Recv()
		if err == io.EOF {
			// El stream ha finalizado de forma natural
			statusChan <- "✅ Stream finalizado por el servidor."
			return
		}
		if err != nil {
			// Manejar errores del stream, incluyendo la cancelación desde el cliente.
			// `context.Canceled` es un error esperado si el usuario para la reproducción.
			if ctx.Err() == context.Canceled {
				statusChan <- "Stream cancelado por el usuario."
			} else {
				statusChan <- fmt.Sprintf("❌ Error durante el streaming: %v", err)
			}
			return
		}

		// Enviar el fragmento de audio al canal para que otro componente lo procese
		// Usamos un select para no bloquear si el contexto se cancela mientras enviamos.
		select {
		case <-ctx.Done():
			statusChan <- "Stream cancelado por el usuario."
			return
		case audioChunksChan <- fragmento.GetData():
			// El fragmento fue enviado exitosamente
		}
	}
}

func (pr *ProcedimientosStreaming) StreamAudioOfSong(request *pb.PeticionStreamDTO, ctx context.Context, audioChunksChan chan<- []byte, statusChan chan<- string) {
	// Es responsabilidad de esta función cerrar el canal de audio cuando termine.
	defer close(audioChunksChan)

	client := pr.client

	// Llamada al método de streaming
	stream, err := client.StremearCancion(ctx, request)
	if err != nil {
		statusChan <- fmt.Sprintf("❌ Error al iniciar el stream: %v", err)
		return
	}

	statusChan <- "🎧 Streaming iniciado. Recibiendo datos..."

	for {
		// Recibir un fragmento de la canción
		fragmento, err := stream.Recv()
		if err == io.EOF {
			// El stream ha finalizado de forma natural
			statusChan <- "✅ Stream finalizado por el servidor."
			return
		}
		if err != nil {
			// Manejar errores del stream, incluyendo la cancelación desde el cliente.
			// `context.Canceled` es un error esperado si el usuario para la reproducción.
			if ctx.Err() == context.Canceled {
				statusChan <- "Stream cancelado por el usuario."
			} else {
				statusChan <- fmt.Sprintf("❌ Error durante el streaming: %v", err)
			}
			return
		}

		// Enviar el fragmento de audio al canal para que otro componente lo procese
		// Usamos un select para no bloquear si el contexto se cancela mientras enviamos.
		select {
		case <-ctx.Done():
			statusChan <- "Stream cancelado por el usuario."
			return
		case audioChunksChan <- fragmento.GetData():
			// El fragmento fue enviado exitosamente
		}
	}
}

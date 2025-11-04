package main

import (
	"log"
	"time"

	"google.golang.org/grpc"

	cli "musis.cliente/grpc-cliente/internal/cli"
	procCanc "musis.cliente/grpc-cliente/pkg/procedimientosCanciones"
	procStream "musis.cliente/grpc-cliente/pkg/procedimientosStreaming"
	pbCanciones "musis.servidordecanciones/grpc-servidor/serviciosCancion"
	pbStreaming "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

func main() {
	connCanc, err := grpc.Dial("localhost:50053", grpc.WithInsecure())
	if err != nil {
		log.Fatalf("no se pudo conectar al servidor gRPC: %v", err)
	}
	defer connCanc.Close()
	clienteCanc := pbCanciones.NewServiciosCancionesClient(connCanc)

	connStream, err := grpc.Dial("localhost:50051", grpc.WithInsecure())
	if err != nil {
		log.Fatalf("no se pudo conectar al servidor gRPC: %v", err)
	}
	defer connStream.Close()
	clienteStream := pbStreaming.NewAudioServiceClient(connStream)

	wrapperCanc := procCanc.New(clienteCanc, 5*time.Second)
	wrapperStream := procStream.New(clienteStream)

	// Lanzar CLI
	cli.Start(wrapperCanc, wrapperStream)
}

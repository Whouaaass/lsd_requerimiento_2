package main

import (
	"fmt"
	"log"
	"os"

	tea "github.com/charmbracelet/bubbletea"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	"musis.cliente/grpc-cliente/config"
	"musis.cliente/grpc-cliente/internal/cli"
	cancionesapi "musis.cliente/grpc-cliente/pkg/canciones_api"
	streamService "musis.cliente/grpc-cliente/pkg/streamingService"
	pbStreaming "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

func main() {
	// Carga la configuración
	cfg := config.Load()

	// Inicializa el cliente grpc de streaming
	connStream, err := grpc.NewClient(cfg.AudioStreamingAPIURL, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("no se pudo conectar al servidor gRPC: %v", err)
	}
	defer connStream.Close()
	log.Printf("Servidor de streaming en dirección %s \n", cfg.AudioStreamingAPIURL)
	clienteStream := pbStreaming.NewAudioServiceClient(connStream)
	streamService := streamService.New(clienteStream)

	// Inicializa el servicio de cancion
	cancionesService := cancionesapi.NewCancionesAPIClient(cfg)

	// Inicializa las vistas de la consola
	model := cli.NewModel(streamService, cancionesService)
	p := tea.NewProgram(model)
	if _, err := p.Run(); err != nil {
		fmt.Printf("Alas, there's been an error: %v", err)
		os.Exit(1)
	}

}

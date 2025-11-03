package grpc

import (
	"fmt"
	"net"

	"google.golang.org/grpc"
	reproduccionesapi "musis.servidordestreaming/grpc-servidor/internal/clients/reproducciones_api"
	"musis.servidordestreaming/grpc-servidor/internal/config"
	grpccontrollers "musis.servidordestreaming/grpc-servidor/internal/grpc_controllers"
	"musis.servidordestreaming/grpc-servidor/internal/server/interceptors"
	"musis.servidordestreaming/grpc-servidor/internal/services"
	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

type Server struct {
    grpcServer *grpc.Server
    port       string
}

func NewServer(cfg config.Config) *Server {
    opts := []grpc.ServerOption{
        grpc.ChainUnaryInterceptor(
            interceptors.RecoveryInterceptor,
        ),
    }

    grpcServer := grpc.NewServer(opts...)

    apiReproducciones := reproduccionesapi.NewReproduccionesAPIClient(cfg)
    audioService := services.NewAudioService(apiReproducciones)
    pb.RegisterAudioServiceServer(grpcServer, grpccontrollers.NewControladorServidorAudio(audioService))

    return &Server{
        grpcServer: grpcServer,
        port:       cfg.GRPCPort,
    }
}

func (s *Server) Serve() {
    lis, err := net.Listen("tcp", fmt.Sprintf(":%s", s.port))
    if err != nil {
        panic(err)
    }
    if err := s.grpcServer.Serve(lis); err != nil {
        panic(err)
    }
}

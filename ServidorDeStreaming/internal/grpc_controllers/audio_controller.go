package grpccontrollers

import (
	"musis.servidordestreaming/grpc-servidor/internal/services"
	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

type ControladorServidorAudio struct {
	pb.UnimplementedAudioServiceServer
	audioService *services.AudioService
}

func NewControladorServidorAudio(audioService *services.AudioService) *ControladorServidorAudio {
	return &ControladorServidorAudio{
		audioService: audioService,
	}
}

// Implementación del procedimiento remoto
func (s *ControladorServidorAudio) EnviarCancionMedianteStream(req *pb.PeticionDTO, stream pb.AudioService_EnviarCancionMedianteStreamServer) error {
	return s.audioService.StreamAudioFile(
		req.Id,
		func(data []byte) error {
			return stream.Send(&pb.FragmentoCancion{Data: data})
		},
	)
}

func (s *ControladorServidorAudio) StremearCancion(req *pb.PeticionStreamDTO, stream pb.AudioService_StremearCancionServer) error {
	return s.audioService.StreamAudioFileFromSong(
		req,
		func(data []byte) error {
			return stream.Send(&pb.FragmentoCancion{Data: data})
		},
	)
}

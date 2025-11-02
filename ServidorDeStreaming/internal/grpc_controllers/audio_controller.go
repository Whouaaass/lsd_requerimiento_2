package grpccontrollers

import (
	"musis.servidordestreaming/grpc-servidor/internal/services"
	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

type ControladorServidorAudio struct {
	pb.UnimplementedAudioServiceServer
}

// Implementación del procedimiento remoto
func (s *ControladorServidorAudio) EnviarCancionMedianteStream(req *pb.PeticionDTO, stream pb.AudioService_EnviarCancionMedianteStreamServer) error {

	return services.StreamAudioFile(
		req.Id,
		func(data []byte) error {
			return stream.Send(&pb.FragmentoCancion{Data: data})
		},
	)
}

func (s *ControladorServidorAudio) StremearCancion(req *pb.CancionDTO, stream pb.AudioService_StremearCancionServer) error {
	return services.StreamAudioFileFromSong(
		req,
		func(data []byte) error {
			return stream.Send(&pb.FragmentoCancion{Data: data})
		},
	)
}

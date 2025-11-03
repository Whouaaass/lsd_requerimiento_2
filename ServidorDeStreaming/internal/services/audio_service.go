package services

import (
	"errors"
	"fmt"
	"io"
	"log"
	"os"
	"time"

	reproduccionesapi "musis.servidordestreaming/grpc-servidor/internal/clients/reproducciones_api"
	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

type AudioService struct {
	reproduccionesAPI *reproduccionesapi.ReproduccionesAPIClient
}

func NewAudioService (apiReproducciones *reproduccionesapi.ReproduccionesAPIClient) *AudioService {
	return &AudioService{
		reproduccionesAPI: apiReproducciones,
	}
}

func (s *AudioService) StreamAudioFileFromSong(req *pb.PeticionStreamDTO, funcionParaEnviarFragmento func([]byte) error) error {

	go s.reproduccionesAPI.RegistrarReproduccion(reproduccionesapi.RegistrarReproduccionPayload{
		IdUsuario: int(req.IdUsuario),
		Cancion: reproduccionesapi.CancionDTO{
			Id: int(req.GetCancion().GetId()),
			Artista: req.GetCancion().Autor,
			Genero: req.GetCancion().Genero,
			Idioma: req.GetCancion().Idioma,
		},
	})

	return streamAudioFile(
		req.GetCancion().RutaAlmacenamiento,
		funcionParaEnviarFragmento,
	)
}

func (*AudioService) StreamAudioFile(id int32, funcionParaEnviarFragmento func([]byte) error) error {
	return errors.New("Función no implementada")
}

func streamAudioFile(ruta string, funcionParaEnviarFragmento func([]byte) error) error {
	file, err := os.Open(ruta)
	if err != nil {
		return fmt.Errorf("no se pudo abrir el archivo: %w", err)
	}
	defer file.Close()

	buffer := make([]byte, 64*1024) // 64 KB se envian por fragmento
	fragmento := 0

	for {
		n, err := file.Read(buffer)
		if err == io.EOF {
			log.Println("Canción enviada completamente desde la fachada.")
			break
		}
		if err != nil {
			return fmt.Errorf("error leyendo el archivo: %w", err)
		}
		fragmento++
		log.Printf("Fragmento #%d leído (%d bytes) y enviando", fragmento, n)

		// ejecutamos la función para enviar el fragmento al cliente
		time.Sleep(time.Second * 1)
		err = funcionParaEnviarFragmento(buffer[:n])
		if err != nil {
			return fmt.Errorf("error enviando el fragmento #%d: %w", fragmento, err)
		}
	}

	return nil
}

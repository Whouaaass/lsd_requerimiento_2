package services

import (
	"errors"
	"fmt"
	"io"
	"log"
	"os"
	"time"

	pb "musis.servidordestreaming/grpc-servidor/serviciosStreaming"
)

func StreamAudioFileFromSong(cancion *pb.CancionDTO, funcionParaEnviarFragmento func([]byte) error) error {
	// TODO: Enviar cancion al servidor de almacenamiento

	return streamAudioFile(
		cancion.RutaAlmacenamiento,
		funcionParaEnviarFragmento,
	)
}

func StreamAudioFile(id int32, funcionParaEnviarFragmento func([]byte) error) error {
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

package capafachada

import (
	capaaccesoadatos "almacenamiento/capaAccesoADatos"
	dtos "almacenamiento/capaFachadaServices/DTOs"
	componnteconexioncola "almacenamiento/componnteConexionCola"
	"almacenamiento/config"
	"fmt"
)

type FachadaAlmacenamiento struct {
	cfg          *config.Config
	repo         *capaaccesoadatos.RepositorioCanciones
	conexionCola *componnteconexioncola.RabbitPublisher
}

// Constructor de la fachada
func NuevaFachadaAlmacenamiento(cfg *config.Config) *FachadaAlmacenamiento {
	fmt.Println("🔧 Inicializando fachada de almacenamiento...")

	repo := capaaccesoadatos.GetRepositorioCanciones()

	conexionCola := componnteconexioncola.NewRabbitPublisher(cfg)	

	return &FachadaAlmacenamiento{
		repo:         repo,
		conexionCola: conexionCola,
		cfg:          &config.Config{},
	}
}

func (thisF *FachadaAlmacenamiento) GuardarCancion(objCancion dtos.CancionAlmacenarDTOInput, data []byte) error {
	thisF.conexionCola.PublicarNotificacion(componnteconexioncola.NotificacionCancion{
		Titulo:  objCancion.Titulo,
		Artista: objCancion.Artista,
		Genero:  objCancion.Genero,
		Idioma:  objCancion.Idioma,
		Mensaje: "Nueva cancion almacenada: " + objCancion.Titulo + " de " + objCancion.Artista,
	})

	// guardar archivo y registro en memmoria
	//delegar en el repositorio
	return thisF.repo.GuardarCancion(objCancion.Titulo, objCancion.Genero, objCancion.Artista, objCancion.Idioma, data)
}

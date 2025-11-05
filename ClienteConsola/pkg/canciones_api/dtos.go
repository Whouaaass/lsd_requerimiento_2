package cancionesapi

// CancionMetadata contiene los campos de texto para la subida de la canción.

type MetadatoCancionDTO struct {
	Id                 int32  `json:"id"`
	Titulo             string `json:"titulo"`
	Genero             string `json:"genero"`
	Artista            string `json:"artista"`
	Idioma             string `json:"idioma"`
	RutaAlmacenamiento string `json:"ruta-almacenamiento"`
}

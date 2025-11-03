package reproduccionesapi

// Cancion represents a song in the reproduction system
type CancionDTO struct {
	Id      int    `json:"id"`
	Artista string `json:"artista"`
	Genero  string `json:"genero"`
	Idioma  string `json:"idioma"`
}

// Reproduccion represents a playback record
type ReproduccionDTO struct {
	ID             int     `json:"id"`
	Reproducciones int     `json:"reproducciones"`
	IDUsuario      int     `json:"idUsuario"`
	Cancion        CancionDTO `json:"cancion"`
}

package reproduccionesapi


// RegistrarReproduccionPayload represents the request to register a reproduction
type RegistrarReproduccionPayload struct {
	IdUsuario int     `json:"idUsuario"`
	Cancion   CancionDTO `json:"cancion"`
}

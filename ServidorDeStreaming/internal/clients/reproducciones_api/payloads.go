package reproduccionesapi

// RegistrarReproduccionPayload represents the request to register a reproduction
type RegistrarReproduccionPayload struct {
	IdUsuario int `json:"idUsuario"`
	IdCancion int `json:"idCancion"`
}

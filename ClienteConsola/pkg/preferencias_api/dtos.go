package preferenciasapi

// --- Estructuras de Respuesta JSON ---

// ArtistaPreferido representa una preferencia de artista
type ArtistaPreferido struct {
	NombreArtista      string `json:"nombreArtista"`
	NumeroPreferencias int    `json:"numeroPreferencias"`
}

// GeneroPreferido representa una preferencia de género
type GeneroPreferido struct {
	NombreGenero       string `json:"nombreGenero"`
	NumeroPreferencias int    `json:"numeroPreferencias"`
}

// IdiomaPreferido representa una preferencia de idioma
type IdiomaPreferido struct {
	NombreIdioma       string `json:"nombreIdioma"`
	NumeroPreferencias int    `json:"numeroPreferencias"`
}

// PreferenciasUsuario es la estructura principal que agrupa todas las preferencias
type PreferenciasUsuario struct {
	IdUsuario            int                `json:"idUsuario"`
	PreferenciasArtistas []ArtistaPreferido `json:"preferenciasArtistas"`
	PreferenciasGeneros  []GeneroPreferido  `json:"preferenciasGeneros"`
	PreferenciasIdiomas  []IdiomaPreferido  `json:"preferenciasIdiomas"`
}

package controlador

import (
	dtos "almacenamiento/capaFachadaServices/DTOs"
	capafachada "almacenamiento/capaFachadaServices/fachada"
	"fmt"
	"io"
	"net/http"
	"os"
)

type ControladorAlmacenamientoCanciones struct {
	fachada *capafachada.FachadaAlmacenamiento
}

// Constructor del Controlador
func NuevoControladorAlmacenamientoCanciones() *ControladorAlmacenamientoCanciones {
	return &ControladorAlmacenamientoCanciones{
		fachada: capafachada.NuevaFachadaAlmacenamiento(),
	}
}

func (thisC *ControladorAlmacenamientoCanciones) AlmacenarAudioCancion(w http.ResponseWriter, r *http.Request) {
	fmt.Print("Almacenando canción...\n")
	if r.Method != http.MethodPost {
		http.Error(w, "Método no permitido", http.StatusMethodNotAllowed)
		return
	}

	r.ParseMultipartForm(50 << 20)
	file, _, err := r.FormFile("archivo")
	if err != nil {
		http.Error(w, "Error leyendo el archivo", http.StatusBadRequest)
		return
	}
	defer file.Close()
	data, _ := io.ReadAll(file)

	//leer los campos del dto
	dto := dtos.CancionAlmacenarDTOInput{
		Titulo:  r.FormValue("titulo"),
		Artista: r.FormValue("artista"),
		Genero:  r.FormValue("genero"),
		Idioma:  r.FormValue("idioma"),
	}

	w.WriteHeader(http.StatusCreated)
	thisC.fachada.GuardarCancion(dto, data)

}

// listar canciojes
func (thisC *ControladorAlmacenamientoCanciones) ListarCanciones(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		http.Error(w, "Método no permitido", http.StatusMethodNotAllowed)
		return
	}

	data, err := os.ReadFile("catalogo.json")
	if err != nil {
		http.Error(w, "No se encontró el catálogo de canciones", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.Write(data)
	fmt.Println("Catálogo enviado al cliente.")

}

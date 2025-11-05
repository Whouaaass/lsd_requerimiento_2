package capaaccesoadatos

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"sync"
	"sync/atomic"
)

var audios_path = "../canciones"
var catalogo_path = "catalogo.json"

type RepositorioCanciones struct {
	mu     sync.Mutex
	lastID int32
}

var (
	instancia *RepositorioCanciones
	once      sync.Once
)

type MetadatoCancion struct {
	ID                 int32  `json:"id"`
	Titulo             string `json:"titulo"`
	Genero             string `json:"genero"`
	Artista            string `json:"artista"`
	Idioma             string `json:"idioma"`
	RutaAlmacenamiento string `json:"ruta-almacenamiento"`
}

// obtiene el ID más alto del catálogo existente
func obtenerUltimoID() int32 {
	data, err := os.ReadFile(catalogo_path)
	if err != nil {
		return 0
	}

	var catalogo []MetadatoCancion
	if err := json.Unmarshal(data, &catalogo); err != nil {
		return 0
	}

	var maxID int32 = 0
	for _, cancion := range catalogo {
		if cancion.ID > maxID {
			maxID = cancion.ID
		}
	}
	return maxID
}

// ✅ Patrón Singleton
func GetRepositorioCanciones() *RepositorioCanciones {
	once.Do(func() {
		instancia = &RepositorioCanciones{
			lastID: obtenerUltimoID(),
		}
	})
	return instancia
}

// ✅ Guarda o actualiza una canción
func (r *RepositorioCanciones) GuardarCancion(titulo, genero, artista, idioma string, data []byte) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	os.MkdirAll(audios_path, os.ModePerm)

	fileName := fmt.Sprintf("%s_%s_%s.mp3", titulo, genero, artista)
	filePath := filepath.Join(audios_path, fileName)

	// Guarda archivo físico (sin duplicar lógica)
	if err := os.WriteFile(filePath, data, 0644); err != nil {
		return fmt.Errorf("error al guardar archivo: %v", err)
	}

	// 🔍 Actualiza catálogo y recibe si fue nuevo o actualizado
	statusMsg, err := r.agregarAlCatalogo(titulo, genero, artista, idioma, filePath)
	if err != nil {
		return fmt.Errorf("error actualizando catálogo: %v", err)
	}

	// Muestra solo el mensaje correcto
	fmt.Println(statusMsg)
	return nil
}

// ✅ Maneja el catálogo sin duplicar metadatos
func (r *RepositorioCanciones) agregarAlCatalogo(titulo, genero, artista, idioma, path string) (string, error) {
	catalogPath := catalogo_path
	var catalogo []MetadatoCancion

	// Leer catálogo actual si existe
	data, err := os.ReadFile(catalogPath)
	if err == nil && len(data) > 0 {
		if err := json.Unmarshal(data, &catalogo); err != nil {
			fmt.Println("⚠️ Error leyendo JSON, se reinicia el catálogo.")
			catalogo = []MetadatoCancion{}
		}
	} else {
		catalogo = []MetadatoCancion{}
	}

	// Buscar si ya existe
	for i, c := range catalogo {
		if c.Titulo == titulo && c.Artista == artista {
			// 🔄 Actualizar metadatos
			catalogo[i].Genero = genero
			catalogo[i].Idioma = idioma
			data, _ := json.MarshalIndent(catalogo, "", "  ")
			os.WriteFile(catalogPath, data, 0644)
			return fmt.Sprintf("🟡 '%s' de '%s' ya existe", titulo, artista), nil
		}
	}

	// ✅ Agregar nueva canción con ID único
	newID := atomic.AddInt32(&r.lastID, 1)
	nueva := MetadatoCancion{
		ID:                 newID,
		Titulo:             titulo,
		Genero:             genero,
		Artista:            artista,
		Idioma:             idioma,
		RutaAlmacenamiento: path,
	}
	catalogo = append(catalogo, nueva)

	data, _ = json.MarshalIndent(catalogo, "", "  ")
	if err := os.WriteFile(catalogPath, data, 0644); err != nil {
		return "", fmt.Errorf("❌ Error escribiendo el catálogo: %v", err)
	}

	return fmt.Sprintf("✅ '%s' de '%s' agregada correctamente al catálogo.", titulo, artista), nil
}

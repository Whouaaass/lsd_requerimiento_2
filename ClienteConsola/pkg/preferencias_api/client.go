package preferenciasapi

import (
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"musis.cliente/grpc-cliente/config"
)

// --- Cliente HTTP ---

type CancionesAPIClient struct {
	baseURL string
	http    *http.Client
}

func NewPreferenciasAPIClient(cfg config.Config) *CancionesAPIClient {
	http := &http.Client{
		Timeout: 10 * time.Second,
	}
	return &CancionesAPIClient{
		http:    http,
		baseURL: cfg.PreferenciasAPIURL,
	}
}

// GetPreferenciasPorUsuario obtiene las preferencias de un usuario por su ID
func (c *CancionesAPIClient) GetPreferenciasPorUsuario(usuarioID int) (*PreferenciasUsuario, error) {

	// Construir la URL completa
	url := fmt.Sprintf("%s/api/preferencias/%d", c.baseURL, usuarioID)

	// Crear la petición GET
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("error al crear la petición: %w", err)
	}

	// Añadir headers (buena práctica)
	req.Header.Set("Accept", "application/json")

	// Ejecutar la petición
	resp, err := c.http.Do(req)
	if err != nil {
		return nil, fmt.Errorf("error al ejecutar la petición: %w", err)
	}
	defer resp.Body.Close()

	// Verificar el código de estado
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("error: la API devolvió un estado no esperado: %s", resp.Status)
	}

	// Decodificar la respuesta JSON
	var preferencias PreferenciasUsuario
	if err := json.NewDecoder(resp.Body).Decode(&preferencias); err != nil {
		return nil, fmt.Errorf("error al decodificar la respuesta JSON: %w", err)
	}

	return &preferencias, nil
}

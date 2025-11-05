package cancionesapi

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"musis.cliente/grpc-cliente/config"
)

type CancionesAPIClient struct {
	baseURL string
	http    *http.Client
}

func NewCancionesAPIClient(cfg config.Config) *CancionesAPIClient {
	http := &http.Client{
		Timeout: 10 * time.Second,
	}
	return &CancionesAPIClient{
		http:    http,
		baseURL: cfg.CancionesAPIURL,
	}
}

// ListarCanciones implementa la solicitud 'listar canciones'.
func (c *CancionesAPIClient) ListarCanciones() ([]MetadatoCancionDTO, error) {

	url := fmt.Sprintf("%s/canciones/listar", c.baseURL)

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")

	resp, err := c.http.Do(req)
	if err != nil {
		return nil, fmt.Errorf("failed to execute request: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("unexpected status code: %d, response: %s", resp.StatusCode, string(body))
	}

	var reproducciones []MetadatoCancionDTO
	if err := json.NewDecoder(resp.Body).Decode(&reproducciones); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return reproducciones, nil
}

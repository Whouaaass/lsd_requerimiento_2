package reproduccionesapi

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"musis.servidordestreaming/grpc-servidor/internal/config"
)

type ReproduccionesAPIClient struct {
	baseURL string
	http    *http.Client
}

func NewReproduccionesAPIClient(cfg config.Config) *ReproduccionesAPIClient {
	http := &http.Client{
		Timeout: 10 * time.Second,
	}
	return &ReproduccionesAPIClient{
		http:    http,
		baseURL: cfg.ReproduccionesAPIURL,
	}
}

/*
// ObtenerReproduccionesDeUsuario retrieves playback records for a specific user
func (c *ReproduccionesAPIClient) ObtenerReproduccionesDeUsuario(userID int) ([]ReproduccionDTO, error) {
	url := fmt.Sprintf("%s/api/reproducciones?user_id=%d", c.baseURL, userID)

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

	var reproducciones []ReproduccionDTO
	if err := json.NewDecoder(resp.Body).Decode(&reproducciones); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return reproducciones, nil
}
*/

// RegistrarReproduccion registers a new playback for a user
func (c *ReproduccionesAPIClient) RegistrarReproduccion(request RegistrarReproduccionPayload) (int, error) {
	url := fmt.Sprintf("%s/api/reproducciones/register", c.baseURL)

	jsonData, err := json.Marshal(request)
	if err != nil {
		return 0, fmt.Errorf("failed to marshal request: %w", err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return 0, fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")

	resp, err := c.http.Do(req)
	if err != nil {
		return 0, fmt.Errorf("failed to execute request: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		body, _ := io.ReadAll(resp.Body)
		return 0, fmt.Errorf("unexpected status code: %d, response: %s", resp.StatusCode, string(body))
	}

	var reproduccionID int
	if err := json.NewDecoder(resp.Body).Decode(&reproduccionID); err != nil {
		return 0, fmt.Errorf("failed to decode response: %w", err)
	}

	return reproduccionID, nil
}

package reproduccionesapi

import (
	"net/http"
	"time"
	"musis.servidordestreaming/grpc-servidor/internal/config"
)

type ReproduccionesAPIClient struct {
	baseURL string
	http *http.Client
}

func NewReproduccionesAPIClient(cfg config.Config) *ReproduccionesAPIClient {
	http := &http.Client{
		Timeout: 10 * time.Second,
	}
	return &ReproduccionesAPIClient{
		http: http,
		baseURL: cfg.ReproduccionesAPIURL,
	}
}

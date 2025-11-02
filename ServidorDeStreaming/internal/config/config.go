package config

import "os"

type Config struct {
    GRPCPort string
    ReproduccionesAPIURL string
}

func Load() Config {
    port := os.Getenv("GRPC_PORT")
    if port == "" {
        port = "50051"
    }
    reproducciones_url := os.Getenv("REPRODUCCIONES_API_URL")
    if reproducciones_url == "" {
   		reproducciones_url = "http://localhost:3002"
    }
    return Config{
    	GRPCPort: port,
    	ReproduccionesAPIURL: reproducciones_url,
    }
}

# Development Docker instructions

Run the streaming gRPC server inside a development container with source bind-mounted so you can edit files locally and see changes when the container restarts.

Prereqs: Docker and docker-compose installed.

Quick start (from the `ServidorDeStreaming` folder):

```pwsh
docker compose -f docker-compose.dev.yml up --build
```

Notes:
- The service reads `GRPC_PORT` (default 50051) and `REPRODUCCIONES_API_URL` (default http://localhost:8082). The compose file sets `REPRODUCCIONES_API_URL` to `http://host.docker.internal:8082` so a backend running on the host is reachable from the container on Windows/Mac. On Linux you may need to adjust this.
- For faster iteration you can run a file-watcher like `air` or `reflex` inside the container and update `command` in `docker-compose.dev.yml`.

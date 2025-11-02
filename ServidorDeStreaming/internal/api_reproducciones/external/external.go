package external

import (
	"encoding/json"
	"fmt"
	"net/http"

	"musis.servidordestreaming/grpc-servidor/internal/api_reproducciones/client"
)

type ExternalClient struct {
    baseURL string
    http    *client.HTTPClient
}

func New(baseURL string, c *client.HTTPClient) *ExternalClient {
    return &ExternalClient{
        baseURL: baseURL,
        http:    c,
    }
}

func (e *ExternalClient) GetUser(id string) (*UserResponse, error) {
    req, _ := http.NewRequest("GET", fmt.Sprintf("%s/users/%s", e.baseURL, id), nil)
    resp, err := e.http.Do(req)
    if err != nil {
        return nil, err
    }
    defer resp.Body.Close()

    var user UserResponse
    if err := json.NewDecoder(resp.Body).Decode(&user); err != nil {
        return nil, err
    }
    return &user, nil
}

package client

import (
    "net/http"
    "time"
)

type HTTPClient struct {
    http *http.Client
}

func New() *HTTPClient {
    return &HTTPClient{
        http: &http.Client{Timeout: 10 * time.Second},
    }
}

func (c *HTTPClient) Do(req *http.Request) (*http.Response, error) {
    return c.http.Do(req)
}

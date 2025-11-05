
```bash
docker run -it --rm   -v $(pwd)/envoy.yaml:/etc/envoy/envoy.yaml --network host   envoyproxy/envoy:v1.28-latest   --config-path /etc/envoy/envoy.yaml
```
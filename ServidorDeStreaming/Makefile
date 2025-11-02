OUT_DIR=.

proto:
	protoc --go_out=$(OUT_DIR) --go-grpc_out=$(OUT_DIR) *.proto

run:
	go run ./main/servidor.go

test:
	go test ./...

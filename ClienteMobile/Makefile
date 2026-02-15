OUT_DIR=.
PROTO_FILE=serviciosStreaming.proto
WEB_OUT_DIR=../Cliente/lib/grpc/stub

proto:
	protoc --go_out=$(OUT_DIR) --go-grpc_out=$(OUT_DIR) $(PROTO_FILE)

proto-web:
	protoc -I=. $(PROTO_FILE) \
	  --js_out=import_style=commonjs:$(WEB_OUT_DIR) \
	  --grpc-web_out=import_style=commonjs,mode=grpcwebtext:$(WEB_OUT_DIR)

run:
	go run ./main/servidor.go

test:
	go test ./...

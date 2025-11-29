PROTO_OUT_DIR=lib/generated
PROTO_DIR=../ServidorDeStreaming
PROTO_FILE=serviciosStreaming.proto

build-proto:
	protoc --dart_out=grpc:$(PROTO_OUT_DIR) \
    -I=$(PROTO_DIR) \
    $(PROTO_FILE)

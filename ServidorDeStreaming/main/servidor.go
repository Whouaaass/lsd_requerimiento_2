package main

import (
	"log"
	"musis.servidordestreaming/grpc-servidor/internal/config"
	grpcserver "musis.servidordestreaming/grpc-servidor/internal/server/grpc"
)

func main() {
	cfg := config.Load();
	srv := grpcserver.NewServer(cfg)
	log.Println("Servidor grpc escuchando en :", cfg.GRPCPort)
	srv.Serve()
}

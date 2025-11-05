package main

import (
	controlador "almacenamiento/capaControladores"
	"almacenamiento/config"
	"fmt"
	"net/http"
)

func main() {
	cfg := config.Load()
	ctrl := controlador.NuevoControladorAlmacenamientoCanciones(&cfg)

	http.HandleFunc("/canciones/almacenamiento", ctrl.AlmacenarAudioCancion)
	http.HandleFunc("/canciones/listar", ctrl.ListarCanciones)

	fmt.Println("✅ Servicio de Tendencias escuchando en el puerto 5000...")
	if err := http.ListenAndServe(":5000", nil); err != nil {
		fmt.Println("Error iniciando el servidor:", err)
	}
}

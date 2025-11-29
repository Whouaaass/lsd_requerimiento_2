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

	mux := http.NewServeMux()

	// se definen las rutas
	mux.HandleFunc("/canciones/almacenamiento", ctrl.AlmacenarAudioCancion)
	mux.HandleFunc("/canciones/listar", ctrl.ListarCanciones)

	// se cubren las rutas con el handler de cors
	handlerWithCors := controlador.EnableCors(mux)

	fmt.Println("✅ Servicio de Tendencias escuchando en el puerto 5000...")
	if err := http.ListenAndServe(":5000", handlerWithCors); err != nil {
		fmt.Println("Error iniciando el servidor:", err)
	}
}

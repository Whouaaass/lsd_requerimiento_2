package cli

import (
	"bufio"
	"os"

	procStream "musis.cliente/grpc-cliente/pkg/streamingService"
)

// Start arranca la interfaz CLI con un wrapper ya autenticado/creado.
func Start(wrapperStreaming *procStream.ProcedimientosStreaming) {
	reader := bufio.NewReader(os.Stdin)
	app := &App{
		wrapperStreaming: wrapperStreaming,
		reader:           reader,
	}
	app.Run()
}

// App mantiene estado mínimo de ejecución (puede extenderse para testing).
type App struct {
	wrapperStreaming *procStream.ProcedimientosStreaming
	reader           *bufio.Reader
}

// Run inicia el loop principal.
func (a *App) Run() {
	for {
		clearTerminal()
		printHeader()
		/*
			if err := a.menuGeneros(); err != nil {
				// errores críticos: mostrar y salir
				printError("Error crítico: " + err.Error())
				pause(a.reader)
				return
			}
		*/
	}
}

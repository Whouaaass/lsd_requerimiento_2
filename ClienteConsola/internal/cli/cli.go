package cli

import (
	"bufio"
	"os"

	procCanc "musis.cliente/grpc-cliente/pkg/procedimientosCanciones"
	procStream "musis.cliente/grpc-cliente/pkg/procedimientosStreaming"
)

// Start arranca la interfaz CLI con un wrapper ya autenticado/creado.
func Start(wrapperCanciones *procCanc.ClienteProcedimientos, wrapperStreaming *procStream.ProcedimientosStreaming) {
	reader := bufio.NewReader(os.Stdin)
	app := &App{
		wrapperCanciones: wrapperCanciones,
		wrapperStreaming: wrapperStreaming,
		reader:           reader,
	}
	app.Run()
}

// App mantiene estado mínimo de ejecución (puede extenderse para testing).
type App struct {
	wrapperCanciones *procCanc.ClienteProcedimientos
	wrapperStreaming *procStream.ProcedimientosStreaming
	reader           *bufio.Reader
}

// Run inicia el loop principal.
func (a *App) Run() {
	for {
		clearTerminal()
		printHeader()

		if err := a.menuGeneros(); err != nil {
			// errores críticos: mostrar y salir
			printError("Error crítico: " + err.Error())
			pause(a.reader)
			return
		}
	}
}

package cli

// menuGeneros muestra la lista de géneros y procesa la selección.
/*
func (a *App) menuGeneros() error {
	generos, err := a.wrapperCanciones.ListarGeneros()
	if err != nil {
		return err
	}

	// principal: mostrar opciones
	printSectionTitle("Selecciona un género para listar canciones")
	fmt.Println("0) Salir")
	for i, g := range generos {
		fmt.Printf("%d) %s\n", i+1, g)
	}
	fmt.Println()

	printPrompt("Selecciona un género:")

	choice, ok := readInt(a.reader)
	if !ok {
		printWarning("Entrada inválida.")
		pause(a.reader)
		return nil
	}

	if choice == 0 {
		goodbye()
		// para salir del programa
		osExit(0)
		return nil
	}
	if choice < 0 || choice > len(generos) {
		printWarning("Opción fuera de rango.")
		pause(a.reader)
		return nil
	}

	generoSel := generos[choice-1]
	return a.menuCancionesGenero(generoSel)
}
*/
/*
// menuCancionesGenero lista canciones de un género y permite ver detalles.
func (a *App) menuCancionesGenero(genero string) error {
	canciones, err := a.wrapperCanciones.ListarCancionesGenero(genero)
	if err != nil {
		return err
	}

	for {
		clearTerminal()
		printHeader()
		printSectionTitle("Género: " + genero)

		if len(canciones) == 0 {
			printInfo("No hay canciones para este género.")
			fmt.Println("0) Volver")
			choice, _ := readInt(a.reader)
			if choice == 0 {
				return nil
			}
			continue
		}

		fmt.Println("0) Volver")
		for i, c := range canciones {
			fmt.Printf("%d) %s\n", i+1, miniMeta(c))
		}
		fmt.Println()

		printPrompt("Seleccione canción (0 para volver):")
		choice, ok := readInt(a.reader)
		if !ok {
			printWarning("Entrada inválida.")
			pause(a.reader)
			continue
		}
		if choice == 0 {
			return nil
		}
		if choice < 1 || choice > len(canciones) {
			printWarning("Opción fuera de rango.")
			pause(a.reader)
			continue
		}

		// Mostrar detalle
		if err := a.menuDetalleCancion(canciones[choice-1]); err != nil {
			// en caso de error al mostrar detalle, volver al listado
			printError("Error mostrando detalle: " + err.Error())
			pause(a.reader)
		}
	}
}
*/

// menuDetalleCancion muestra detalles y opciones (volver, reproducir placeholder).
/*
func (a *App) menuDetalleCancion(c *pb.Cancion) error {
	for {
		clearTerminal()
		printHeader()
		printSectionTitle("Detalle de la canción")
		//printMetaDetailed(c)
		fmt.Println("1) Reproducir")
		fmt.Println("0) Volver")
		fmt.Println("9) Salir")
		printPrompt("Selecciona una opción:")

		choice, ok := readInt(a.reader)
		if !ok {
			printWarning("Entrada inválida.")
			pause(a.reader)
			continue
		}
		switch choice {
		case 1:
			// Llamada al servicio de streaming para reproducir la canción
			// Esta función recibirá datos, reproducirá y mostrará progreso.
			clearTerminal()
			printHeader()
			printSectionTitle("Reproducción")

			// Mensaje inicial
			printInfo("Iniciando streaming... presiona Ctrl+C para cancelar si es necesario")

			// Crear canal para recibir progreso
			a.menuReproduccionCancion(c)

		case 0:
			return nil
		case 9:
			goodbye()
			osExit(0)
		default:
			printWarning("Opción no reconocida.")
			pause(a.reader)
		}
	}
}
*/
/*
func (a *App) menuReproduccionCancion(c *pb.Cancion) error {
	var wg sync.WaitGroup
	ctx, cancelCtx := context.WithCancel(context.Background())
	audioChunksChan := make(chan []byte, 100)
	statusChan := make(chan string, 10)
	errorChan := make(chan error, 10)

	clearTerminal()
	printHeader()
	printSectionTitle("Reproducción de la canción")
	//printMetaDetailed(c)
	fmt.Println()
	printPrompt("Presiona ENTER para salir...")
	cursorUp(1)

	// Iniciar el streaming en segundo plano
	wg.Add(1)
	go func() {
		a.wrapperStreaming.StreamAudio(c.Id, ctx, audioChunksChan, statusChan)
		wg.Done()
	}()

	// Iniciar el reproductor en segundo plano
	wg.Add(1)
	go func() {
		err := audioplayer.PlayStream(ctx, audioChunksChan, statusChan)
		if err != nil {
			errorChan <- err
		}
		wg.Done()
	}()

	wg.Add(1)
	go func() {
		a.reader.ReadString('\n')
		cursorUp(1)
		statusChan <- "Parando reproducción..."
		cancelCtx()
		statusChan <- "Contexto cerrado..."
		wg.Done()
	}()

	go logStatusChannel(ctx, statusChan)
	go logErrorChannel(ctx, errorChan)

	// Esperar a que termine el stream y el reproductor
	wg.Wait()
	close(errorChan)
	close(statusChan)

	return nil
}
*/

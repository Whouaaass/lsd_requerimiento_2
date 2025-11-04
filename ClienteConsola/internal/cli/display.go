package cli

import (
	"context"
	"fmt"
	"os"
	"time"

	pb "musis.servidordecanciones/grpc-servidor/serviciosCancion"
)

const (
	ansiReset     = "\033[0m"
	ansiBold      = "\033[1m"
	ansiCyan      = "\033[36m"
	ansiYellow    = "\033[33m"
	ansiGreen     = "\033[32m"
	ansiRed       = "\033[31m"
	ansiBlue      = "\033[34m"
	ansiLightGray = "\033[37m"
	clearScreen   = "\033[H\033[2J"
)

// helpers de impresión
func clearTerminal() {
	fmt.Print(clearScreen)
}

func printHeader() {
	fmt.Printf("%s%sMUSIS - Cliente CLI%s\n", ansiBold, ansiCyan, ansiReset)
	fmt.Println("Explora canciones por género")
}

func printSectionTitle(s string) {
	fmt.Printf("%s%s%s\n", ansiBold, s, ansiReset)
	fmt.Println("---------------------------------------------")
}

func printPrompt(s string) {
	fmt.Printf("%s%s %s", ansiYellow, s, ansiReset)
}

func printInfo(s string) {
	fmt.Printf("%s%s%s\n", ansiBlue, s, ansiReset)
}

func printWarning(s string) {
	fmt.Printf("%s%s%s\n", ansiYellow, s, ansiReset)
}

func printError(s string) {
	fmt.Printf("%s%s%s\n", ansiRed, s, ansiReset)
}

func cursorUp(n int) {
	fmt.Printf("\033[%dA", n)
}

func cursorEnd() {
	fmt.Printf("\033[0J")
}

func goodbye() {
	fmt.Printf("\n%sGracias por usar MUSIS. Hasta luego!%s%s\n\n", ansiBold, ansiGreen, ansiReset)
}

var osExit = func(code int) {
	os.Exit(code)
}

// miniMeta presenta título, autor/banda y duración breve.
func miniMeta(c *pb.Cancion) string {
	duracion := secsToMinSec(int(c.DuracionS))
	album := c.Album
	if album == "" {
		album = "—"
	}
	autor := c.Autor
	if autor == "" {
		autor = "—"
	}
	return fmt.Sprintf("%s — %s (%s) [%s]", c.Titulo, autor, album, duracion)
}

func printMetaDetailed(c *pb.Cancion) {
	fmt.Printf("%sTitulo:%s\t\t %s%s\n", ansiBold, ansiReset, c.Titulo, ansiReset)
	fmt.Printf("%sBanda/Autor:%s\t %s%s\n", ansiBold, ansiReset, c.Autor, ansiReset)
	fmt.Printf("%sAlbum:%s\t\t %s%s\n", ansiBold, ansiReset, c.Album, ansiReset)
	fmt.Printf("%sGenero:%s\t\t %s%s\n\n", ansiBold, ansiReset, c.Genero, ansiReset)
}

func secsToMinSec(s int) string {
	min := s / 60
	sec := s % 60
	return fmt.Sprintf("%02d:%02d", min, sec)
}

// formatea duración como mm:ss o hh:mm:ss si es mayor a una hora
func formatDuration(d time.Duration) string {
	total := int(d.Seconds())
	h := total / 3600
	m := (total % 3600) / 60
	s := total % 60
	if h > 0 {
		return fmt.Sprintf("%02d:%02d:%02d", h, m, s)
	}
	return fmt.Sprintf("%02d:%02d", m, s)
}

// Spinner returns a function that cycles through spinner characters
func spinner() func() string {
	frames := []string{"|", "/", "-", "\\"}
	pos := 0
	return func() string {
		val := frames[pos]
		pos = (pos + 1) % len(frames) // wrap around
		return val
	}
}

// this function is for logging status messages from the audio player
func logStatusChannel(ctx context.Context, statusChan chan string) {
	for {
		select {
		case <-ctx.Done():
			return
		case status := <-statusChan:
			fmt.Printf("\r%-60s\r", status)
		}
	}
}

func logErrorChannel(ctx context.Context, errorChan chan error) {
	for {
		select {
		case <-ctx.Done():
			return
		case err := <-errorChan:
			fmt.Printf("\r%-60s", err)
		}
	}
}

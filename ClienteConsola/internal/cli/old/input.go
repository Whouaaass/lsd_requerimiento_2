package cli

import (
	"bufio"
	"strconv"
	"strings"
)

// readInt lee una línea y la convierte a int. Retorna (valor, ok)
func readInt(reader *bufio.Reader) (int, bool) {
	line, err := reader.ReadString('\n')
	if err != nil {
		return 0, false
	}
	line = strings.TrimSpace(line)
	if line == "" {
		return 0, false
	}
	n, err := strconv.Atoi(line)
	if err != nil {
		return 0, false
	}
	return n, true
}

func pause(reader *bufio.Reader) {
	printPrompt("Presiona Enter para continuar...")
	reader.ReadString('\n')
}

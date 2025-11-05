package cli

import (
	"fmt"
	"strings"
)

func (m model) View() string {
	switch m.state {
	case generosView:
		return getGenerosView(&m)
	case testStreamingView:
		return getTestStreamingView(&m)
	}

	return "SIN VISTA\n\npresiona q para salir\n"
}

func getGenerosView(m *model) string {
	return "GENEROS VIEW"
}

func getTestStreamingView(m *model) string {

	var b strings.Builder

	b.WriteString("--- Spotifake Streaming Test ---\n\n")

	// Show playback status
	if m.isPlaying {
		b.WriteString("▶ Playing\n")
	} else {
		b.WriteString("■ Stopped\n")
	}

	// Show status and error messages
	b.WriteString(fmt.Sprintf("Status: %s\n", m.statusMessage))
	if m.errorMessage != "" {
		b.WriteString(fmt.Sprintf("Error: %s\n", m.errorMessage))
	}

	b.WriteString("\n--- Controls ---\n")
	if m.isPlaying {
		b.WriteString("Press 's' to Stop\n")
	} else {
		b.WriteString("Press 'p' to Play\n")
	}
	b.WriteString("Press 'q' to Quit\n")

	return b.String()
}

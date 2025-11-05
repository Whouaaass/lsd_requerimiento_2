package cli

import (
	"fmt"

	tea "github.com/charmbracelet/bubbletea"
)

func (m model) fetchCancionesCmd() tea.Cmd {
	return func() tea.Msg {
		// This runs in a separate goroutine.
		canciones, err := m.cancionesService.ListarCanciones()

		// Send the result (or error) back to Update as a message.
		return cancionesLoadedMsg{canciones: canciones, err: err}
	}
}

func (m *model) loadPreferenciasCmd() tea.Cmd {
	return func() tea.Msg {
		// Llamar al cliente API
		prefs, err := m.preferenciasClient.GetPreferenciasPorUsuario(int(m.user.Id))
		if err != nil {
			// Devolver un mensaje de error
			return errorMsg{fmt.Errorf("API error: %w", err)}
		}
		// Devolver el mensaje de éxito con los datos
		return preferenciasLoadedMsg{preferencias: prefs}
	}
}

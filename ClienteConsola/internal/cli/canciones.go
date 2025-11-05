package cli

import tea "github.com/charmbracelet/bubbletea"

func (m model) fetchCancionesCmd() tea.Cmd {
	return func() tea.Msg {
		// This runs in a separate goroutine.
		canciones, err := m.cancionesService.ListarCanciones()

		// Send the result (or error) back to Update as a message.
		return cancionesLoadedMsg{canciones: canciones, err: err}
	}
}

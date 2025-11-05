package cli

import tea "github.com/charmbracelet/bubbletea"

func goTo(m *model, state uint) (tea.Model, tea.Cmd) {
	switch state {
	case menuView:
		m.state = menuView
		m.canciones = nil
		m.cursor = 0
		return m, nil
	case catalogoView:
		m.state = catalogoView
		m.canciones = nil
		m.cursor = 0
		return m, m.fetchCancionesCmd()
	case loginView:
		m.state = loginView
		m.user = nil                 // Limpia ID
		m.loginError = ""            // Limpia errores
		m.usernameInput.SetValue("") // Limpia campos
		m.passwordInput.SetValue("")
		m.cursor = 0     // Resetea cursor del menú (para la próxima vez)
		m.focusIndex = 0 // Pone foco en el input de usuario
		m.usernameInput.Focus()
		m.passwordInput.Blur()
		return m, nil
	default:
		m.state = state
		m.cursor = 0
		return m, nil
	}

}

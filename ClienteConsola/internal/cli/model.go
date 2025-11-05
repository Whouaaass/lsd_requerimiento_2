package cli

import (
	"context"
	"fmt"

	"github.com/charmbracelet/bubbles/textinput"
	tea "github.com/charmbracelet/bubbletea"
	"musis.cliente/grpc-cliente/pkg/auth"
	cancionesapi "musis.cliente/grpc-cliente/pkg/canciones_api"
	preferenciasapi "musis.cliente/grpc-cliente/pkg/preferencias_api"
	"musis.cliente/grpc-cliente/pkg/streamingService"
)

const (
	loginView uint = iota
	menuView
	generosView
	catalogoView
	cancionView
	preferenciasView // Nueva vista
)

type model struct {
	state uint
	user  *auth.User

	// Data
	canciones []cancionesapi.MetadatoCancionDTO

	// Login
	focusIndex    int // 0 = username, 1 = password
	usernameInput textinput.Model
	passwordInput textinput.Model
	loginError    string

	// services
	audioStreamService *streamingService.ProcedimientosStreaming
	cancionesService   *cancionesapi.CancionesAPIClient
	preferenciasClient *preferenciasapi.CancionesAPIClient

	// UI and state management
	ctx             context.Context                  // Context for managing the stream lifecycle
	cancel          context.CancelFunc               // Function to cancel the stream
	cursor          int                              // Posición actual del cursor
	selectedCancion *cancionesapi.MetadatoCancionDTO // La canción seleccionada
	isPlaying       bool
	statusMessage   string
	errorMessage    string

	isLoadingPreferencias bool
	preferencias          *preferenciasapi.PreferenciasUsuario

	// Channels for async communication with the streaming goroutine
	statusChan chan string
	errorChan  chan error
	doneChan   chan struct{} // Signals that streaming is finished
}

func NewModel(
	audioStreamService *streamingService.ProcedimientosStreaming,
	cancionesService *cancionesapi.CancionesAPIClient,
	preferenciasClient *preferenciasapi.CancionesAPIClient) model {

	user := textinput.New()
	user.Placeholder = "Usuario"
	user.Focus() // El primer input empieza enfocado
	user.CharLimit = 32
	user.Width = 30

	pass := textinput.New()
	pass.Placeholder = "Contraseña"
	pass.EchoMode = textinput.EchoPassword // Oculta la contraseña
	pass.CharLimit = 32
	pass.Width = 30

	return model{
		state:         loginView,
		focusIndex:    0, // El foco empieza en el username
		usernameInput: user,
		passwordInput: pass,
		loginError:    "",

		audioStreamService: audioStreamService,
		cancionesService:   cancionesService,
		preferenciasClient: preferenciasClient,
		user:               nil,
		isPlaying:          false,
		statusMessage:      "",
	}
}
func (m model) Init() tea.Cmd {
	return textinput.Blink
}

func (m model) Update(msg tea.Msg) (tea.Model, tea.Cmd) {
	var cmd tea.Cmd

	switch msg := msg.(type) {
	case tea.KeyMsg:
		key := msg.String()
		switch m.state {
		case loginView:
			// Si el mensaje no es para cambiar de control,
			// pasarlo al input enfocado
			switch key {
			case "ctrl+c":
				return m, tea.Quit

			case "tab", "shift+tab", "up", "down":
				// Cambiar el foco
				m.focusIndex = (m.focusIndex + 1) % 2
				if m.focusIndex == 0 {
					m.usernameInput.Focus()
					m.passwordInput.Blur()
				} else {
					m.usernameInput.Blur()
					m.passwordInput.Focus()
				}
				return m, nil

			case "enter":
				// --- Aquí va tu lógica de autenticación ---
				user := m.usernameInput.Value()
				pass := m.passwordInput.Value()

				authUser, err := auth.AutenticarUsuario(user, pass)
				if err != nil {
					m.loginError = err.Error()
					return m, nil
				}

				m.loginError = ""
				m.user = authUser
				return goTo(&m, menuView)
			}

			// Pasa el mensaje (escritura) al input correcto
			if m.focusIndex == 0 {
				m.usernameInput, cmd = m.usernameInput.Update(msg)
			} else {
				m.passwordInput, cmd = m.passwordInput.Update(msg)
			}
			return m, cmd
		case menuView:
			switch key {
			case "b", "esc":
				return goTo(&m, menuView)
			case "ctrl+c", "q":
				return m, tea.Quit

			case "up", "k":
				if m.cursor > 0 {
					m.cursor--
				}

			case "down", "j":
				if m.cursor < 2 {
					m.cursor++
				}

			case "enter":
				switch m.cursor {
				case 0:
					m.state = preferenciasView
					m.errorMessage = ""
					// Cargar preferencias si no se han cargado
					if m.preferencias == nil && !m.isLoadingPreferencias {
						m.isLoadingPreferencias = true
						m.statusMessage = "Cargando preferencias..."
						return m, m.loadPreferenciasCmd()
					}
					return m, nil
				case 1: // Ir a Catálogo de Canciones
					m.state = catalogoView
					m.canciones = nil // Limpia para forzar recarga
					m.cursor = 0      // Resetea cursor para la lista de canciones
					// Asumo que tienes este comando de nuestra conversación anterior
					return m, m.fetchCancionesCmd()

				case 2: // Cerrar Sesión
					return goTo(&m, loginView)
				}
			}

		case cancionView:
			switch key {
			// --- Controles de Navegación ---
			case "esc", "b": // Volver al catálogo
				// Detener la música si se está reproduciendo antes de salir
				if m.isPlaying && m.cancel != nil {
					m.cancel()
				}
				return goTo(&m, catalogoView)

			case "ctrl+c", "q":
				// Asegurarse de detener el stream antes de salir
				if m.isPlaying && m.cancel != nil {
					m.cancel()
				}
				return m, tea.Quit

			// --- Controles de Streaming (Fusionados) ---
			case "p":
				if !m.isPlaying && m.selectedCancion != nil { // Solo reproduce si hay una canción
					m.isPlaying = true
					m.statusMessage = fmt.Sprintf("Cargando %s...", m.selectedCancion.Titulo)
					m.errorMessage = ""
					m.ctx, m.cancel = context.WithCancel(context.Background())
					m.statusChan = make(chan string, 10)
					m.errorChan = make(chan error, 10)
					m.doneChan = make(chan struct{})

					// Asumo que tu runStreamingTask usará m.selectedCancion
					go m.runStreamingTask()

					return m, m.listenForMessagesCmd()
				}
				return m, nil

			case "s":
				if m.isPlaying && m.cancel != nil {
					m.statusMessage = "Deteniendo stream..."
					m.cancel() // Señaliza a la goroutine que pare
				}
				return m, nil
			}

		case catalogoView:
			switch key {
			case "esc", "b": // Volver al catálogo
				return goTo(&m, menuView)
			case "q", "ctrl+c":
				return m, tea.Quit

			case "up", "k": // Mover cursor hacia arriba
				if m.cursor > 0 {
					m.cursor--
				}
			case "down", "j": // Mover cursor hacia abajo
				// Asegúrate de que las canciones se hayan cargado antes de chequear el largo
				if m.canciones != nil && m.cursor < len(m.canciones)-1 {
					m.cursor++
				}

			case "enter": // Seleccionar la canción
				// Solo si las canciones están cargadas y la lista no está vacía
				if len(m.canciones) > 0 {
					// 1. Guardar la canción seleccionada
					m.selectedCancion = &m.canciones[m.cursor]
					// 2. Cambiar a la vista de detalle
					m.state = cancionView
				}
				return m, nil
			}
		case preferenciasView:
			switch key {
			case "r": // Recargar preferencias
				m.isLoadingPreferencias = true
				m.preferencias = nil
				m.statusMessage = "Recargando preferencias..."
				m.errorMessage = ""
				return m, m.loadPreferenciasCmd()
			}

		default:
			switch key {
			case "ctrl+c", "q":
				return m, tea.Quit
			}
		}

	// --- Handle async messages from the streaming task ---

	case statusMsg:
		// Received a status update.
		m.statusMessage = string(msg)
		// Return a command to continue listening.
		return m, m.listenForMessagesCmd()

	case errorMsg:
		// Received an error.
		m.errorMessage = msg.err.Error()
		// Return a command to continue listening.
		// Determinar el contexto del error
		if m.isLoadingPreferencias {
			m.isLoadingPreferencias = false
			m.statusMessage = "Error al cargar preferencias."
		}
		// Si el error ocurrió durante el streaming, seguir escuchando
		if m.isPlaying {
			return m, m.listenForMessagesCmd()
		}
		return m, m.listenForMessagesCmd()

	case playbackFinishedMsg:
		// Stream finished cleanly.
		m.isPlaying = false
		m.statusMessage = "Playback finished."
		m.cleanup() // Clean up context and channels.
		// No new command, stop listening.
		return m, nil
		// --- Handle our new async message ---
	case cancionesLoadedMsg:
		if msg.err != nil {
			m.errorMessage = fmt.Sprintf("Error cargando canciones: %v", msg.err)
		} else {
			m.canciones = msg.canciones
			m.errorMessage = "" // Clear any previous error
		}
		return m, nil
	}

	if m.state == loginView {
		var cmdUser, cmdPass tea.Cmd
		m.usernameInput, cmdUser = m.usernameInput.Update(msg)
		m.passwordInput, cmdPass = m.passwordInput.Update(msg)
		return m, tea.Batch(cmdUser, cmdPass)
	}

	return m, nil
}

// listenForMessagesCmd returns a command that waits for the next message
// from the streaming goroutines.
func (m *model) listenForMessagesCmd() tea.Cmd {
	return func() tea.Msg {
		select {
		case status := <-m.statusChan:
			return statusMsg(status)
		case err := <-m.errorChan:
			return errorMsg{err}
		case <-m.doneChan:
			return playbackFinishedMsg{}
		}
	}
}

// cleanup closes channels and clears context.
func (m *model) cleanup() {
	if m.cancel != nil {
		m.cancel = nil
		m.ctx = nil
	}
	// Closing channels ensures the listening goroutine exits.
	if m.statusChan != nil {
		close(m.statusChan)
		m.statusChan = nil
	}
	if m.errorChan != nil {
		close(m.errorChan)
		m.errorChan = nil
	}
	if m.doneChan != nil {
		close(m.doneChan)
		m.doneChan = nil
	}
}

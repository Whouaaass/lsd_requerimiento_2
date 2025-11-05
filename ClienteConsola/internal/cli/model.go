package cli

import (
	"context"

	tea "github.com/charmbracelet/bubbletea"
	"musis.cliente/grpc-cliente/pkg/streamingService"
)

const (
	generosView uint = iota
	cancionesView
	cancionView

	testStreamingView
)

type model struct {
	state              uint
	audioStreamService *streamingService.ProcedimientosStreaming
	idUsuario          int32

	// UI and state management
	ctx           context.Context    // Context for managing the stream lifecycle
	cancel        context.CancelFunc // Function to cancel the stream
	isPlaying     bool
	statusMessage string
	errorMessage  string

	// Channels for async communication with the streaming goroutine
	statusChan chan string
	errorChan  chan error
	doneChan   chan struct{} // Signals that streaming is finished
}

func NewModel(audioStreamService *streamingService.ProcedimientosStreaming) model {
	return model{
		state:              testStreamingView,
		audioStreamService: audioStreamService,
		idUsuario:          1,
		isPlaying:          false,
		statusMessage:      "",
	}
}
func (m model) Init() tea.Cmd {
	return nil
}

func (m model) Update(msg tea.Msg) (tea.Model, tea.Cmd) {

	switch msg := msg.(type) {
	case tea.KeyMsg:
		key := msg.String()
		switch m.state {
		case testStreamingView:
			switch key {
			case "ctrl+c", "q":
				// Ensure stream is stopped before quitting.
				if m.isPlaying && m.cancel != nil {
					m.cancel()
				}
				return m, tea.Quit

			case "p":
				// Start playing only if not already playing.
				if !m.isPlaying {
					m.isPlaying = true
					m.statusMessage = "Starting stream..."
					m.errorMessage = ""
					m.ctx, m.cancel = context.WithCancel(context.Background())

					// Create channels for this streaming session
					m.statusChan = make(chan string, 10)
					m.errorChan = make(chan error, 10)
					m.doneChan = make(chan struct{})

					// Start the streaming task in a goroutine.
					go m.runStreamingTask()

					// Return a command to listen for the first message.
					return m, m.listenForMessagesCmd()
				}
				return m, nil

			case "s":
				// Stop the stream if it's playing.
				if m.isPlaying && m.cancel != nil {
					m.statusMessage = "Stopping stream..."
					m.cancel() // Signal goroutines to stop.
				}
				return m, nil
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
		return m, m.listenForMessagesCmd()

	case playbackFinishedMsg:
		// Stream finished cleanly.
		m.isPlaying = false
		m.statusMessage = "Playback finished."
		m.cleanup() // Clean up context and channels.
		// No new command, stop listening.
		return m, nil
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

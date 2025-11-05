package cli

// statusMsg is a message to update the status line.
type statusMsg string

// errorMsg is a message to report an error.
type errorMsg struct{ err error }

func (e errorMsg) Error() string {
	return e.err.Error()
}

// playbackFinishedMsg signals that streaming and playback have
// completed successfully or been stopped.
type playbackFinishedMsg struct{}

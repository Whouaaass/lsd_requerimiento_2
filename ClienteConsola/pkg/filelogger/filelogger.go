package filelogger

import (
	"fmt"
	"os"
	"path/filepath"
	"sync"
	"time"

	"musis.cliente/grpc-cliente/config"
)

// FileLogger writes timestamped log lines to a file. It is safe for concurrent use.
type FileLogger struct {
	mu   sync.Mutex
	f    *os.File
	Path string
}

// New creates or opens the file at path for appending. If path is empty the
// function uses "logs.txt".
func New(path string) (*FileLogger, error) {
	if path == "" {
		path = "logs.txt"
	}
	dir := filepath.Dir(path)
	if dir != "" && dir != "." {
		if err := os.MkdirAll(dir, 0o755); err != nil {
			return nil, fmt.Errorf("create log dir: %w", err)
		}
	}
	f, err := os.OpenFile(path, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0o644)
	if err != nil {
		return nil, fmt.Errorf("open log file: %w", err)
	}
	return &FileLogger{f: f, Path: path}, nil
}

// NewFromConfig creates a FileLogger using the given config (uses Config.LogFile).
// If the config value is empty it falls back to "logs.txt".
func NewFromConfig(cfg config.Config) (*FileLogger, error) {
	path := cfg.LogFile
	if path == "" {
		path = "logs.txt"
	}
	return New(path)
}

func (l *FileLogger) log(level, msg string) error {
	l.mu.Lock()
	defer l.mu.Unlock()
	if l.f == nil {
		return fmt.Errorf("logger closed")
	}
	ts := time.Now().Format(time.RFC3339)
	_, err := fmt.Fprintf(l.f, "%s [%s] %s\n", ts, level, msg)
	if err != nil {
		return fmt.Errorf("write log: %w", err)
	}
	return nil
}

// Info logs an informational message.
func (l *FileLogger) Info(msg string) error { return l.log("INFO", msg) }

// Error logs an error message.
func (l *FileLogger) Error(msg string) error { return l.log("ERROR", msg) }

// Close closes the underlying file. Subsequent writes return an error.
func (l *FileLogger) Close() error {
	l.mu.Lock()
	defer l.mu.Unlock()
	if l.f == nil {
		return nil
	}
	err := l.f.Close()
	l.f = nil
	return err
}

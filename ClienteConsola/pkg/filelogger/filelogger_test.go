package filelogger

import (
	"os"
	"strings"
	"testing"

	"musis.cliente/grpc-cliente/config"
)

func TestFileLoggerCreatesFileAndWrites(t *testing.T) {
	tmp := "test_logs.txt"
	_ = os.Remove(tmp)
	l, err := New(tmp)
	if err != nil {
		t.Fatalf("New error: %v", err)
	}
	defer func() {
		l.Close()
		_ = os.Remove(tmp)
	}()

	if err := l.Info("hello world"); err != nil {
		t.Fatalf("Info error: %v", err)
	}
	// ensure flushed and closed to read reliably
	l.Close()

	b, err := os.ReadFile(tmp)
	if err != nil {
		t.Fatalf("read file: %v", err)
	}
	s := string(b)
	if !strings.Contains(s, "hello world") {
		t.Fatalf("log content missing: %q", s)
	}
}

func TestNewFromConfig(t *testing.T) {
	cfg := config.Config{LogFile: "cfg_logs.txt"}
	_ = os.Remove(cfg.LogFile)
	l, err := NewFromConfig(cfg)
	if err != nil {
		t.Fatalf("NewFromConfig error: %v", err)
	}
	if err := l.Info("from cfg"); err != nil {
		t.Fatalf("Info error: %v", err)
	}
	l.Close()
	b, err := os.ReadFile(cfg.LogFile)
	if err != nil {
		t.Fatalf("read file: %v", err)
	}
	if !strings.Contains(string(b), "from cfg") {
		t.Fatalf("log content missing from cfg file: %q", string(b))
	}
	_ = os.Remove(cfg.LogFile)
}

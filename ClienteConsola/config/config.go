package config

import (
	"os"
)

type Config struct {
	AudioStreamingAPIURL string
	// LogFile is the path to the log file used by the filelogger package.
	// It can be set via the LOG_FILE environment variable. If empty, the
	// filelogger will fall back to `logs.txt`.
	LogFile string
}

func Load() Config {
	return Config{
		AudioStreamingAPIURL: readVariable("STREAMING_API_URL", "localhost:50051"),
		LogFile:              readVariable("LOG_FILE", "logs.txt"),
	}
}

// Reads the variable and set's it's default
func readVariable(env_name string, default_value string) string {
	variable := os.Getenv(env_name)

	if variable == "" {
		return default_value
	}
	return variable
}

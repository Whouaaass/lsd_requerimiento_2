package config

import (
	"os"
)

type Config struct {
	RabbitMQURL string
}

func Load() Config {
	return Config{
		RabbitMQURL: readVariable("RABBITMQ_CANCIONES_URL", "amqp://admin:1234@127.0.0.1:5672/"),
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

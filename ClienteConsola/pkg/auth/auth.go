package auth

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

// Formato esperado: "usuario:contraseña:id" por línea.
// Devuelve (true, User) en éxito, o (false, nil) en fallo.
func AutenticarUsuario(username, password string) (*User, error) {
	const authFile = "auth.txt"

	data, err := os.ReadFile(authFile)
	if err != nil {
		return nil, err
	}

	lines := strings.SplitSeq(string(data), "\n")
	for line := range lines {
		// Ahora esperamos 3 partes: user:pass:id
		line = strings.TrimSpace(line)
		parts := strings.SplitN(line, ":", 3)
		if len(parts) == 3 {
			userFromFile := parts[0]
			passFromFile := parts[1]
			idFromFile := parts[2] // ID como string

			// Comprueba credenciales
			if userFromFile == username && passFromFile == password {
				// Credenciales correctas, convertir ID
				id, err := strconv.Atoi(idFromFile)

				if err != nil {
					// El ID en el archivo está mal formateado
					err = fmt.Errorf("error de formato de ID para usuario: %s", username)
					return nil, err
				}

				// ¡Éxito!
				return &User{
					Id:       int32(id),
					Username: userFromFile,
				}, nil
			}
		}
	}

	// No se encontró coincidencia
	err = fmt.Errorf("usuario o contraseña incorrectos")
	return nil, err
}

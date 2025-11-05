package cli

import (
	"fmt"
	"strings"
)

func (m model) View() string {
	switch m.state {
	case loginView:
		return getLoggingView(&m)
	case menuView:
		return getMenuView(&m)
	case catalogoView:
		return getCatalogoView(&m)
	case cancionView:
		return getDetalleView(&m)
	case preferenciasView:
		return m.preferenciasView()
	}

	return "SIN VISTA\n\npresiona q para salir\n"
}

func getLoggingView(m *model) string {
	var b strings.Builder

	b.WriteString("INICIO DE SESIÓN\n\n")

	// Dibuja los inputs
	b.WriteString(m.usernameInput.View())
	b.WriteString("\n")
	b.WriteString(m.passwordInput.View())
	b.WriteString("\n\n")

	// Muestra el error de login si existe
	if m.loginError != "" {
		// (Opcional: puedes usar un estilo de 'lipgloss' para el color rojo)
		b.WriteString(m.loginError + "\n\n")
	}

	// Ayuda
	b.WriteString("(Usa 'Tab' para cambiar de campo)\n")
	b.WriteString("(Presiona 'Enter' para ingresar)\n")
	b.WriteString("(Presiona 'Ctrl+C' para salir)\n")

	return b.String()
}

func getCatalogoView(m *model) string {
	var b strings.Builder

	// --- Lógica de carga (sin cambios) ---
	/*
		if m.canciones == nil {
			var err error
			m.canciones, err = m.cancionesService.ListarCanciones()
			if err != nil {
				fmt.Fprintf(&b, "Error al cargar el catálogo:\n%v", err)
				return b.String()
			}
		}
	*/

	// --- Lógica de renderizado (Modificada) ---
	if len(m.canciones) == 0 {
		b.WriteString("El catálogo está vacío.")
		return b.String()
	}

	b.WriteString("Catálogo de Canciones (Usa ↑/↓ para mover, 'Enter' para seleccionar):\n\n")

	for i, cancion := range m.canciones {
		// Variable para el cursor
		cursor := "  " // Espacio en blanco si no está seleccionado

		if m.cursor == i {
			cursor = "> " // Flecha si está seleccionado
		}

		// Escribir la línea con el cursor y la info de la canción
		fmt.Fprintf(&b, "%s%s - %s (Género: %s)\n",
			cursor,
			cancion.Titulo,
			cancion.Artista,
			cancion.Genero,
		)
	}

	b.WriteString("\n  (b, Esc) Volver al menu principal")
	b.WriteString("\n (Presiona 'q' para salir)")
	return b.String()
}

func getDetalleView(m *model) string {
	var b strings.Builder

	b.WriteString("Detalle de la Canción:\n\n")

	// --- Detalles de la canción ---
	if m.selectedCancion != nil {
		fmt.Fprintf(&b, "  Título:   %s\n", m.selectedCancion.Titulo)
		fmt.Fprintf(&b, "  Artista:  %s\n", m.selectedCancion.Artista)
		fmt.Fprintf(&b, "  Género:   %s\n", m.selectedCancion.Genero)
		fmt.Fprintf(&b, "  Idioma:   %s\n", m.selectedCancion.Idioma)
	} else {
		b.WriteString("  No se ha seleccionado ninguna canción.\n")
	}

	b.WriteString("\n--- Controles de Reproducción ---\n\n")

	// --- Estado del Streaming (de getTestStreamingView) ---
	if m.isPlaying {
		b.WriteString("  ▶ Reproduciendo\n")
	} else {
		b.WriteString("  ■ Detenido\n")
	}

	// --- Mensajes de Estado y Error (de getTestStreamingView) ---
	b.WriteString(fmt.Sprintf("  Estado: %s\n", m.statusMessage))
	if m.errorMessage != "" {
		b.WriteString(fmt.Sprintf("  Error: %s\n", m.errorMessage))
	}

	// --- Controles (Fusionados) ---
	b.WriteString("\n\n--- Controles ---\n")
	if m.isPlaying {
		b.WriteString("  (s) Detener\n")
	} else {
		b.WriteString("  (p) Reproducir\n")
	}
	b.WriteString("  (b, Esc) Volver al catálogo\n")
	b.WriteString("  (q) Salir\n")

	return b.String()
}

func getMenuView(m *model) string {
	var b strings.Builder

	// Saludo al usuario que inició sesión
	b.WriteString(fmt.Sprintf("¡Hola, %v! \n\n", m.user.Username))
	b.WriteString("Menú Principal\n")
	b.WriteString("(Usa ↑/↓ para mover, 'Enter' para seleccionar):\n\n")

	// Opciones del menú
	opciones := []string{
		"Preferencias",
		"Catálogo de Canciones",
		"Cerrar Sesión",
	}

	for i, opcion := range opciones {
		cursor := "  " // Espacio
		if m.cursor == i {
			cursor = "> " // Flecha
		}
		fmt.Fprintf(&b, "%s%s\n", cursor, opcion)
	}

	b.WriteString("\n  (Presiona 'q' para salir)")
	return b.String()
}

func (m model) preferenciasView() string {
	var b strings.Builder
	b.WriteString("--- Mis Preferencias ---\n\n")

	if m.isLoadingPreferencias {
		b.WriteString("Cargando...\n")
		return b.String()
	}

	if m.preferencias == nil {
		b.WriteString("No se han podido cargar las preferencias.\n")
		b.WriteString("Presiona 'r' para reintentar.\n")
		return b.String()
	}

	// Renderizar preferencias
	b.WriteString(fmt.Sprintf("Preferencias para Usuario ID: %d\n\n", m.preferencias.IdUsuario))

	b.WriteString("Artistas Favoritos:\n")
	if len(m.preferencias.PreferenciasArtistas) == 0 {
		b.WriteString("  (Ninguno)\n")
	}
	for _, p := range m.preferencias.PreferenciasArtistas {
		b.WriteString(fmt.Sprintf("  - %s (%d veces)\n", p.NombreArtista, p.NumeroPreferencias))
	}

	b.WriteString("\nGéneros Favoritos:\n")
	if len(m.preferencias.PreferenciasGeneros) == 0 {
		b.WriteString("  (Ninguno)\n")
	}
	for _, p := range m.preferencias.PreferenciasGeneros {
		b.WriteString(fmt.Sprintf("  - %s (%d veces)\n", p.NombreGenero, p.NumeroPreferencias))
	}

	b.WriteString("\nIdiomas Favoritos:\n")
	if len(m.preferencias.PreferenciasIdiomas) == 0 {
		b.WriteString("  (Ninguno)\n")
	}
	for _, p := range m.preferencias.PreferenciasIdiomas {
		b.WriteString(fmt.Sprintf("  - %s (%d veces)\n", p.NombreIdioma, p.NumeroPreferencias))
	}

	b.WriteString("\n\nPresiona 'r' para recargar.\n")
	return b.String()
}

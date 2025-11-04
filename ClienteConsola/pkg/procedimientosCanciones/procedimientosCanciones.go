package procedimientosCanciones

import (
	"context"
	"fmt"
	"time"

	pb "musis.servidordecanciones/grpc-servidor/serviciosCancion"
)

// ClienteProcedimientos encapsula el cliente gRPC y configuración común
type ClienteProcedimientos struct {
	client     pb.ServiciosCancionesClient
	rpcTimeout time.Duration
}

// New crea un nuevo wrapper para procedimientos de canciones.
// Si timeout es 0 se usa 5s por defecto.
func New(client pb.ServiciosCancionesClient, timeout time.Duration) *ClienteProcedimientos {
	if timeout <= 0 {
		timeout = 5 * time.Second
	}
	return &ClienteProcedimientos{
		client:     client,
		rpcTimeout: timeout,
	}
}

// BuscarCancion llama al RPC BuscarCancion y devuelve la canción encontrada o error.
func (c *ClienteProcedimientos) BuscarCancion(titulo string) (*pb.Cancion, error) {
	ctx, cancel := context.WithTimeout(context.Background(), c.rpcTimeout)
	defer cancel()

	req := &pb.PeticionCancionDTO{Titulo: titulo}
	resp, err := c.client.BuscarCancion(ctx, req)
	if err != nil {
		return nil, fmt.Errorf("error llamando BuscarCancion: %w", err)
	}

	if resp == nil {
		return nil, fmt.Errorf("respuesta vacía al buscar canción")
	}

	if resp.Codigo != 200 {
		return nil, fmt.Errorf("no se encontró la canción: %s (codigo %d)", resp.Mensaje, resp.Codigo)
	}

	if resp.ObjCancion == nil {
		return nil, fmt.Errorf("respuesta sin objeto Cancion")
	}

	return resp.ObjCancion, nil
}

// BuscarCancionId llama al RPC BuscarCancionId y devuelve la canción encontrada o error.
func (c *ClienteProcedimientos) BuscarCancionId(id int32) (*pb.Cancion, error) {
	ctx, cancel := context.WithTimeout(context.Background(), c.rpcTimeout)
	defer cancel()

	req := &pb.PeticionCancionIdDTO{Id: id}
	resp, err := c.client.BuscarCancionId(ctx, req)
	if err != nil {
		return nil, fmt.Errorf("error llamando BuscarCancionId: %w", err)
	}

	if resp == nil {
		return nil, fmt.Errorf("respuesta vacía al buscar canción por id")
	}

	if resp.Codigo != 200 {
		return nil, fmt.Errorf("no se encontró la canción: %s (codigo %d)", resp.Mensaje, resp.Codigo)
	}

	if resp.ObjCancion == nil {
		return nil, fmt.Errorf("respuesta sin objeto Cancion")
	}

	return resp.ObjCancion, nil
}

// ListarCancionesGenero llama al RPC ListarCancionesGenero y devuelve la lista de canciones o error.
func (c *ClienteProcedimientos) ListarCancionesGenero(genero string) ([]*pb.Cancion, error) {
	ctx, cancel := context.WithTimeout(context.Background(), c.rpcTimeout)
	defer cancel()

	req := &pb.PeticionCancionesGeneroDTO{Genero: genero}
	resp, err := c.client.ListarCancionesGenero(ctx, req)
	if err != nil {
		return nil, fmt.Errorf("error llamando ListarCancionesGenero: %w", err)
	}

	if resp == nil {
		return nil, fmt.Errorf("respuesta vacía al listar canciones por género")
	}

	if resp.Codigo != 200 {
		return nil, fmt.Errorf("no se encontraron canciones para el género '%s': %s (codigo %d)", genero, resp.Mensaje, resp.Codigo)
	}

	if resp.Canciones == nil {
		return []*pb.Cancion{}, nil
	}

	return resp.Canciones, nil
}

// ListarGeneros llama al RPC ListarGeneros y devuelve la lista de géneros o error.
func (c *ClienteProcedimientos) ListarGeneros() ([]string, error) {
	ctx, cancel := context.WithTimeout(context.Background(), c.rpcTimeout)
	defer cancel()

	req := &pb.Vacio{}
	resp, err := c.client.ListarGeneros(ctx, req)
	if err != nil {
		return nil, fmt.Errorf("error llamando ListarGeneros: %w", err)
	}

	if resp == nil {
		return nil, fmt.Errorf("respuesta vacía al listar géneros")
	}

	if resp.Codigo != 200 {
		return nil, fmt.Errorf("no se pudo obtener la lista de géneros: %s (codigo %d)", resp.Mensaje, resp.Codigo)
	}

	if resp.Generos == nil {
		return []string{}, nil
	}

	return resp.Generos, nil
}

// FormatoCancion retorna una representación legible de la canción (útil en CLI).
func (c *ClienteProcedimientos) FormatoCancion(canc *pb.Cancion) string {
	if canc == nil {
		return "<canción nula>"
	}
	return fmt.Sprintf("Id: %d\nTítulo: %s\nAutor: %s\nAlbum: %s\nDuración (s): %d\nGénero: %s\n",
		canc.Id, canc.Titulo, canc.Autor, canc.Album, canc.DuracionS, canc.Genero)
}

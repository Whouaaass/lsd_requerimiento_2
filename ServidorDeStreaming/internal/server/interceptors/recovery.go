package interceptors

import (
	"context"
	"fmt"
	"runtime/debug"

	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

// RecoveryInterceptor captura panics dentro de las llamadas gRPC y devuelve un error interno.
// Evita que el servidor se caiga si ocurre un panic en cualquier handler.
func RecoveryInterceptor(
    ctx context.Context,
    req interface{},
    info *grpc.UnaryServerInfo,
    handler grpc.UnaryHandler,
) (resp interface{}, err error) {

    // Función defer que captura cualquier panic
    defer func() {
        if r := recover(); r != nil {
            // Logueamos el panic y el stack trace
            fmt.Printf("[PANIC] %v\nStack trace:\n%s\n", r, debug.Stack())

            // Convertimos el panic en un error gRPC con código Internal
            err = status.Errorf(codes.Internal, "internal server error")
        }
    }()

    // Continuamos con la ejecución normal del handler
    return handler(ctx, req)
}


// RecoveryStreamInterceptor captures panics within gRPC stream calls and returns an internal error.
// Prevents the server from crashing if a panic occurs in any stream handler.
func RecoveryStreamInterceptor(
    srv interface{},
    stream grpc.ServerStream,
    info *grpc.StreamServerInfo,
    handler grpc.StreamHandler,
) (err error) {

    // Defer function that captures any panic
    defer func() {
        if r := recover(); r != nil {
            // Log the panic and stack trace
            fmt.Printf("[PANIC] %v\nStack trace:\n%s\n", r, debug.Stack())

            // Convert the panic into a gRPC error with Internal code
            err = status.Errorf(codes.Internal, "internal server error")
        }
    }()

    // Continue with normal execution of the stream handler
    return handler(srv, stream)
}

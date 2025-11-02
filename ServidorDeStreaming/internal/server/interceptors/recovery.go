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

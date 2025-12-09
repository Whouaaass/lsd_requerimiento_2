package unicauca.edu.co.pagos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicauca.edu.co.pagos.dtos.pagoPeticionDTO;
import unicauca.edu.co.pagos.dtos.pagoRespuestaDTO;
import unicauca.edu.co.pagos.dtos.tokenRespuestaDTO;
import unicauca.edu.co.pagos.service.pagoService;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class pagosController {

    private final pagoService pagoService;

    public pagosController(pagoService pagoService) {
        this.pagoService = pagoService;
    }

    // GET /api/pagos/token?usuario=juan
    /**
     * Genera un token, con el cual el usuario será identificado en sus transacciones
     */
    @GetMapping("/token")
    public ResponseEntity<tokenRespuestaDTO> generarToken(@RequestParam String usuario) {
        String token = pagoService.generarToken(usuario);
        return ResponseEntity.ok(new tokenRespuestaDTO(token));
    }

    // POST /api/pagos/pago
    /**
     * Hace un pago
     */
    @PostMapping("/pago")
    public ResponseEntity<pagoRespuestaDTO> registrarPago(@RequestBody pagoPeticionDTO dto) {
        try {
            boolean exito = pagoService.registrarPago(dto);

            if (!exito) {
                // fallo simulado de pago
                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new pagoRespuestaDTO(false, "Pago fallido (simulado), intente nuevamente"));
            }

            return ResponseEntity.ok(new pagoRespuestaDTO(true, "Pago exitoso"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new pagoRespuestaDTO(false, e.getMessage()));

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new pagoRespuestaDTO(false, e.getMessage()));
        }
    }

    // (Opcional) para verificar desde Postman
    /**
     * Ve el total
     */
    @GetMapping("/total")
    public ResponseEntity<Integer> totalUsuario(@RequestParam String usuario) {
        return ResponseEntity.ok(pagoService.obtenerTotalUsuario(usuario));
    }
}
package unicauca.edu.co.pagos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class pago {

    private String usuario;
    private String cancionId;
    private int valor;
    private String token;
    private LocalDateTime fecha;

    public pago(String usuario, String cancionId, int valor, String token) {
        this.usuario = usuario;
        this.cancionId = cancionId;
        this.valor = valor;
        this.token = token;
        this.fecha = LocalDateTime.now();
    }
}

package unicauca.edu.co.pagos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class pagoRespuestaDTO {
    private boolean exito;
    private String mensaje;
}

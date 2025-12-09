package unicauca.edu.co.pagos.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class pagoPeticionDTO {
    private String token;
    private String usuario;
    private String cancionId;
    private int valor;

}
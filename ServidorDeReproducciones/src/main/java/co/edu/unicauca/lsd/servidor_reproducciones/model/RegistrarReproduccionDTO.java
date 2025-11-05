package co.edu.unicauca.lsd.servidor_reproducciones.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegistrarReproduccionDTO {
    @NotNull
    private long idUsuario;

    @NotNull
    private long idCancion;
}

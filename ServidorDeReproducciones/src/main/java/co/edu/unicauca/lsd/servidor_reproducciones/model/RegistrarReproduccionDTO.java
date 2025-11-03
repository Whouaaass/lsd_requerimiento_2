package co.edu.unicauca.lsd.servidor_reproducciones.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrarReproduccionDTO {

    @NotNull
    private Long IdUsuario;
    @NotNull
    private CancionDTO cancion;

}

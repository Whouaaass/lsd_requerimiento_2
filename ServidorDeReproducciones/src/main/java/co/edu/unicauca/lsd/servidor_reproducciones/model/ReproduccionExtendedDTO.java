package co.edu.unicauca.lsd.servidor_reproducciones.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReproduccionExtendedDTO {


    private Long id;

    @NotNull
    private Long reproducciones;

    @NotNull
    private Long idUsuario;

    private Long idCancion;


    
}

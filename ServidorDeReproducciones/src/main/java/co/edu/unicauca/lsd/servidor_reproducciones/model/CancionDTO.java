package co.edu.unicauca.lsd.servidor_reproducciones.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancionDTO {
    @NotNull
    private Long id;

    @NotNull
    private String artista;

    @NotNull
    private String genero;

    @NotNull
    private String idioma;
}

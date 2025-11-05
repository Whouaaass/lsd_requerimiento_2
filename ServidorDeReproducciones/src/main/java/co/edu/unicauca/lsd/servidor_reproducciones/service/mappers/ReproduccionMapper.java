package co.edu.unicauca.lsd.servidor_reproducciones.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;

@Mapper(componentModel = "spring")
public interface ReproduccionMapper {

    // Mapea entidad → DTO
    @Mapping(target = "idCancion", source = "idCancion")
    @Mapping(target = "idUsuario", source = "idUsuario")
    ReproduccionDTO toReproduccionDTO(Reproduccion entity);

    // Mapea DTO → entidad
    @Mapping(target = "idCancion", source = "idCancion")
    @Mapping(target = "idUsuario", source = "idUsuario")
    Reproduccion toReproduccion(ReproduccionDTO dto);

  

    @Mapping(source = "idCancion", target = "idCancion")
    ReproduccionExtendedDTO toReproduccionExtendedDTO(Reproduccion entity);


}

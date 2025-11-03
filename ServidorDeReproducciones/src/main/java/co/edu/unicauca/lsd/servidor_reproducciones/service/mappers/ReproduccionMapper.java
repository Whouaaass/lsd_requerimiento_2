package co.edu.unicauca.lsd.servidor_reproducciones.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Cancion;
import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;

@Mapper(componentModel = "spring")
public interface ReproduccionMapper {

    @Mapping(target = "idCancion", source = "cancion.id")
    ReproduccionDTO toReproduccionDTO(Reproduccion entity);

    @Mapping(target = "cancion", source = "idCancion", qualifiedByName = "idToCancion")
    Reproduccion toReproduccion(ReproduccionDTO dto);

    @Named("idToCancion")
    default Cancion idToCancion(Long id) {
        if (id == null) {
            return null;
        }
        Cancion cancion = new Cancion();
        cancion.setId(id);
        return cancion;
    }

    ReproduccionExtendedDTO toReproduccionExtendedDTO(Reproduccion entity);

}
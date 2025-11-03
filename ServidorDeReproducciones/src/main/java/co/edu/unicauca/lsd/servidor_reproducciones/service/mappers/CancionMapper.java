package co.edu.unicauca.lsd.servidor_reproducciones.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Cancion;
import co.edu.unicauca.lsd.servidor_reproducciones.model.CancionDTO;

@Mapper(componentModel = "spring")
public interface CancionMapper {
    
    CancionMapper INSTANCE = Mappers.getMapper(CancionMapper.class);

    CancionDTO toCancionDTO(Cancion entity);

    Cancion toCancion(CancionDTO entity);
}

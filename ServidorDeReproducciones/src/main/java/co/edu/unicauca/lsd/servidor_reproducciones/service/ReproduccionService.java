package co.edu.unicauca.lsd.servidor_reproducciones.service;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;
import co.edu.unicauca.lsd.servidor_reproducciones.model.RegistrarReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.repos.ReproduccionRepository;
import co.edu.unicauca.lsd.servidor_reproducciones.service.mappers.ReproduccionMapper;
import jakarta.validation.Valid;
import co.edu.unicauca.lsd.servidor_reproducciones.model.RegistrarReproduccionDTO;

import co.edu.unicauca.lsd.servidor_reproducciones.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReproduccionService {

    private final ReproduccionRepository reproduccionRepository;
    private final ReproduccionMapper reproduccionMapper;

    public ReproduccionService(final ReproduccionRepository reproduccionRepository,
                               final ReproduccionMapper reproduccionMapper) {
        this.reproduccionRepository = reproduccionRepository;
        this.reproduccionMapper = reproduccionMapper;
    }

    public List<ReproduccionExtendedDTO> findAll() {
        final List<Reproduccion> reproduccions = reproduccionRepository.findAll(Sort.by("id"));
        return reproduccions.stream()
                .map(reproduccionMapper::toReproduccionExtendedDTO)
                .toList();
    }

    public ReproduccionDTO get(final Long id) {
        return reproduccionRepository.findById(id)
                .map(reproduccionMapper::toReproduccionDTO)
                .orElseThrow(NotFoundException::new);
    }

    public List<ReproduccionExtendedDTO> findByUserId(final Long userId) {
        final List<Reproduccion> reproduccions = reproduccionRepository.findByIdUsuario(userId);
        return reproduccions.stream()
                .map(reproduccionMapper::toReproduccionExtendedDTO)
                .toList();
    }

   
    @Transactional
    public Long create(final RegistrarReproduccionDTO reproduccionDTO) {
        System.out.println("🎧 Iniciando la creación de reproducción");

        Long idCancion = reproduccionDTO.getIdCancion();
        Long idUsuario = reproduccionDTO.getIdUsuario();

        Optional<Reproduccion> optReproduccion = 
            reproduccionRepository.findByIdCancionAndIdUsuario(idCancion, idUsuario);

        Reproduccion reproduccion;
        if (optReproduccion.isPresent()) {
            reproduccion = optReproduccion.get();
            reproduccion.setReproducciones(reproduccion.getReproducciones() + 1);
            System.out.println("Reproducción existente incrementada.");
        } else {
            reproduccion = new Reproduccion();
            reproduccion.setIdUsuario(idUsuario);
            reproduccion.setIdCancion(idCancion);
            reproduccion.setReproducciones(1L);
            System.out.println("Nueva reproducción registrada.");
        }

        return reproduccionRepository.save(reproduccion).getId();
    }


    public void update(final Long id, final ReproduccionDTO reproduccionDTO) {
        final Reproduccion reproduccion = reproduccionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reproduccionRepository.save(reproduccion);
    }

    public void delete(final Long id) {
        final Reproduccion reproduccion = reproduccionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reproduccionRepository.delete(reproduccion);
    }
}

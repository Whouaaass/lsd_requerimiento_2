package co.edu.unicauca.lsd.servidor_reproducciones.service;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Cancion;
import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;
import co.edu.unicauca.lsd.servidor_reproducciones.model.RegistrarReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.repos.CancionRepository;
import co.edu.unicauca.lsd.servidor_reproducciones.repos.ReproduccionRepository;
import co.edu.unicauca.lsd.servidor_reproducciones.service.mappers.CancionMapper;
import co.edu.unicauca.lsd.servidor_reproducciones.service.mappers.ReproduccionMapper;
import co.edu.unicauca.lsd.servidor_reproducciones.exception.NotFoundException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReproduccionService {

    private final ReproduccionRepository reproduccionRepository;
    private final CancionRepository cancionRepository;
    private final ReproduccionMapper reproduccionMapper;
    private final CancionMapper cancionMapper;

    public ReproduccionService(final ReproduccionRepository reproduccionRepository,
            final CancionRepository cancionRepository,
            final ReproduccionMapper reproduccionMapper,
            final CancionMapper cancionMapper) {
        this.reproduccionRepository = reproduccionRepository;
        this.cancionRepository = cancionRepository;
        this.reproduccionMapper = reproduccionMapper;
        this.cancionMapper = cancionMapper;
    }

    public List<ReproduccionExtendedDTO> findAll() {
        final List<Reproduccion> reproduccions = reproduccionRepository.findAll(Sort.by("id"));
        return reproduccions.stream()
                .map(reproduccionMapper::toReproduccionExtendedDTO)
                .toList();
    }

    public ReproduccionDTO get(final Long id) {
        return reproduccionRepository.findById(id)
                .map(reproduccion -> reproduccionMapper.toReproduccionDTO(reproduccion))
                .orElseThrow(NotFoundException::new);
    }

    public List<ReproduccionExtendedDTO> findByUserId(final Long userId) {
        final List<Reproduccion> reproduccions = reproduccionRepository.findByIdUsuario(userId);
        return reproduccions.stream()
                .map(reproduccionMapper::toReproduccionExtendedDTO)
                .toList();
    }

    public Long create(final ReproduccionDTO reproduccionDTO) {
        final Reproduccion reproduccion = reproduccionMapper.toReproduccion(reproduccionDTO);
        return reproduccionRepository.save(reproduccion).getId();
    }

    @Transactional
    public Long create(final RegistrarReproduccionDTO reproduccionDTO) {
        System.out.println("Iniciando la creación de reproduccion");

        Long cancionId = reproduccionDTO.getCancion().getId();
        Long usuarioId = reproduccionDTO.getIdUsuario();

        // Try to find existing reproduction first
        Optional<Reproduccion> optReproduccion = reproduccionRepository.findByCancionIdAndIdUsuario(cancionId,
                usuarioId);

        if (optReproduccion.isPresent()) {
            // If exists, just increment
            Reproduccion reproduccion = optReproduccion.get();
            reproduccion.setReproducciones(reproduccion.getReproducciones() + 1);
            Reproduccion saved = reproduccionRepository.save(reproduccion);
            return saved.getId();
        } else {
            // If doesn't exist, check and handle the song
            Cancion cancion;
            if (!cancionRepository.existsById(cancionId)) {
                System.out.println("No existe la cancion, creando...");
                final Cancion nuevaCancion = cancionMapper.toCancion(reproduccionDTO.getCancion());
                cancion = cancionRepository.save(nuevaCancion);
            } else {
                System.out.println("Existe la cancion");
                cancion = cancionRepository.getReferenceById(cancionId);
            }

            // Create new reproduction
            Reproduccion reproduccion = new Reproduccion();
            reproduccion.setCancion(cancion);
            reproduccion.setIdUsuario(usuarioId);
            reproduccion.setReproducciones(1L);

            Reproduccion saved = reproduccionRepository.save(reproduccion);
            return saved.getId();
        }
    }

    public void update(final Long id, final ReproduccionDTO reproduccionDTO) {
        final Reproduccion reproduccion = reproduccionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // mapToEntity(reproduccionDTO, reproduccion);
        reproduccionRepository.save(reproduccion);
    }

    public void delete(final Long id) {
        final Reproduccion reproduccion = reproduccionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reproduccionRepository.delete(reproduccion);
    }

}

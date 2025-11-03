package co.edu.unicauca.lsd.servidor_reproducciones.repos;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Cancion;
import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ReproduccionRepository extends JpaRepository<Reproduccion, Long> {
    List<Reproduccion> findByIdUsuario(Long userId);
    Optional<Reproduccion> findByCancionAndIdUsuario(Cancion cancion, Long userId);
       Optional<Reproduccion> findByCancionIdAndIdUsuario(Long cancionId, Long idUsuario);
}

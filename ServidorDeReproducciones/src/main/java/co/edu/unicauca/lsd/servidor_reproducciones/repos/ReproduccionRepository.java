package co.edu.unicauca.lsd.servidor_reproducciones.repos;


import co.edu.unicauca.lsd.servidor_reproducciones.domain.Reproduccion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ReproduccionRepository extends JpaRepository<Reproduccion, Long> {
      // Devuelve todas las reproducciones de un usuario
    List<Reproduccion> findByIdUsuario(Long idUsuario);

    // Busca una reproducción específica por canción y usuario
    Optional<Reproduccion> findByIdCancionAndIdUsuario(Long idCancion, Long idUsuario);
}

package co.edu.unicauca.lsd.servidor_reproducciones.repos;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Canciones;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CancionesRepository extends JpaRepository<Canciones, Long> {
}

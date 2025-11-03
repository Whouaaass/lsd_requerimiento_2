package co.edu.unicauca.lsd.servidor_reproducciones.repos;

import co.edu.unicauca.lsd.servidor_reproducciones.domain.Cancion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CancionRepository extends JpaRepository<Cancion, Long> {
}

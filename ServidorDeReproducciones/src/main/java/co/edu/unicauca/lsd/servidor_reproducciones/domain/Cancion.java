package co.edu.unicauca.lsd.servidor_reproducciones.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "Canciones")
public class Cancion {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String artista;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private String idioma;

    @OneToMany(mappedBy = "cancion")
    private Set<Reproduccion> reproducciones = new HashSet<>();

}

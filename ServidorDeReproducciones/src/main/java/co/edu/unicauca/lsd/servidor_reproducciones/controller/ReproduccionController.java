package co.edu.unicauca.lsd.servidor_reproducciones.controller;

import co.edu.unicauca.lsd.servidor_reproducciones.model.RegistrarReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.service.ReproduccionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/reproducciones", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReproduccionController {

    private final ReproduccionService reproduccionService;

    public ReproduccionController(final ReproduccionService reproduccionService) {
        this.reproduccionService = reproduccionService;
    }

    @GetMapping
    public ResponseEntity<List<ReproduccionExtendedDTO>> getAllReproduccions(
            @Param("user_id") final Long user_id) {
        if (user_id != null) {
            return ResponseEntity.ok(reproduccionService.findByUserId(user_id));
        }
        return ResponseEntity.ok(reproduccionService.findAll());    
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReproduccionDTO> getReproduccion(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(reproduccionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createReproduccion(
            @RequestBody @Valid final ReproduccionDTO reproduccionDTO) {
        final Long createdId = reproduccionService.create(reproduccionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> registerReproduccion(
            @RequestBody @Valid final RegistrarReproduccionDTO reproduccionDTO) {
        final Long registeredId = reproduccionService.create(reproduccionDTO);
        return new ResponseEntity<>(registeredId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateReproduccion(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ReproduccionDTO reproduccionDTO) {
        reproduccionService.update(id, reproduccionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteReproduccion(@PathVariable(name = "id") final Long id) {
        reproduccionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

package co.edu.unicauca.lsd.servidor_reproducciones.controller;

import co.edu.unicauca.lsd.servidor_reproducciones.model.RegistrarReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.model.ReproduccionExtendedDTO;
import co.edu.unicauca.lsd.servidor_reproducciones.service.ReproduccionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reproducciones")
@CrossOrigin(origins = "*") // permite llamadas desde otros microservicios
public class ReproduccionController {

    @Autowired
    private ReproduccionService reproduccionService;

    //Registrar una reproducción
    @PostMapping("/register")
    @ApiResponse(responseCode = "201", description = "Reproducción registrada exitosamente")
    public ResponseEntity<Long> registerReproduccion(
            @RequestBody @Valid final RegistrarReproduccionDTO reproduccionDTO) {

        final Long registeredId = reproduccionService.create(reproduccionDTO);
        return new ResponseEntity<>(registeredId, HttpStatus.CREATED);
    }

    // Consultar reproducciones por usuario
    @GetMapping("/listar")
    @ApiResponse(responseCode = "200", description = "Listado de reproducciones del usuario")
    public ResponseEntity<List<ReproduccionExtendedDTO>> listarPorUsuario(@RequestParam("user_id") Long idUsuario) {
        return ResponseEntity.ok(reproduccionService.findByUserId(idUsuario));
    }

    // Consultar todas las reproducciones (solo para pruebas)
    @GetMapping("/todas")
    @ApiResponse(responseCode = "200", description = "Listado completo de reproducciones")
    public ResponseEntity<List<ReproduccionExtendedDTO>> listarTodas() {
        return ResponseEntity.ok(reproduccionService.findAll());
    }
}

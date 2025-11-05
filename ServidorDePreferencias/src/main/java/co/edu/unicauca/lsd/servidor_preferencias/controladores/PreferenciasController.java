package co.edu.unicauca.lsd.servidor_preferencias.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.DTO.PreferenciasDTORespuesta;
import co.edu.unicauca.lsd.servidor_preferencias.fachadaServices.services.IPreferenciasService;

@RestController()
@RequestMapping("/api/preferencias")
@CrossOrigin(origins = "*") // permite llamadas desde otros microservicios
public class PreferenciasController {

    @Autowired
    private IPreferenciasService preferenciaService;

    @GetMapping("")
    public String index() {
        return "Working PreferenciasController";
    }

    @GetMapping("/{user_id}")
    public PreferenciasDTORespuesta obtenerPreferencias(@PathVariable("user_id") Integer id) {
        return preferenciaService.getReferencias(id);
    }
}

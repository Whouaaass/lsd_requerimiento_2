package co.edu.unicauca.lsd.servidor_preferencias.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "\"Hello Servidor Preferencias!\"";
    }

}

package co.edu.unicauca.lsd.servidor_reproducciones.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "\"Hello World!\"";
    }

}

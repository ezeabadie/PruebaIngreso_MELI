package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la ruta raíz de la aplicación.
 * Redirige a la documentación Swagger UI.
 */
@Controller
public class HomeController {

    /**
     * Redirige la raíz "/" a Swagger UI.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }
}


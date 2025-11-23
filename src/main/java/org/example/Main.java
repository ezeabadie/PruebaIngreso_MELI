package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ========================================
 * APLICACIÓN PRINCIPAL
 * ========================================
 *
 * Sistema de Detección de Mutantes - Examen MercadoLibre
 *
 * Endpoints disponibles:
 * - POST http://localhost:8080/mutant
 * - GET  http://localhost:8080/stats
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - H2 Console: http://localhost:8080/h2-console
 *
 * @author GitHub Copilot
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "🧬 Mutant Detection API - ONLINE\n" +
                "========================================\n" +
                "Swagger UI: http://localhost:8080/swagger-ui.html\n" +
                "H2 Console: http://localhost:8080/h2-console\n" +
                "========================================\n");
    }
}


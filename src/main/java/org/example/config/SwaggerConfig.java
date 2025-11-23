package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API.
 *
 * Acceso a Swagger UI:
 * http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detection API")
                        .version("1.0.0")
                        .description(
                                "API REST para detección de mutantes basada en secuencias de ADN.\n\n" +
                                "**Problema:** Magneto quiere reclutar mutantes para luchar contra los X-Men.\n\n" +
                                "**Solución:** Sistema que detecta si un humano es mutante analizando su ADN.\n\n" +
                                "**Reglas:**\n" +
                                "- Un humano es mutante si tiene **MÁS DE UNA** secuencia de 4 letras iguales\n" +
                                "- Las secuencias pueden estar en dirección: horizontal (→), vertical (↓), diagonal (↘), diagonal (↗)\n" +
                                "- El ADN se representa como una matriz NxN con valores: A, T, C, G\n\n" +
                                "**Endpoints:**\n" +
                                "- `POST /mutant`: Verifica si un ADN es mutante\n" +
                                "- `GET /stats`: Obtiene estadísticas de verificaciones"
                        )
                        .contact(new Contact()
                                .name("GitHub Copilot")
                                .email("support@example.com")
                                .url("https://github.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                );
    }
}


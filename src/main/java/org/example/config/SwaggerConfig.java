package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ========================================
 * CONFIGURACIÓN DE SWAGGER/OPENAPI
 * ========================================
 *
 * Esta clase configura la documentación interactiva de la API usando Swagger UI.
 *
 * URLs de acceso:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/api-docs
 * - OpenAPI YAML: http://localhost:8080/api-docs.yaml
 *
 * Swagger proporciona:
 * ✅ Documentación interactiva de endpoints
 * ✅ Pruebas de API sin necesidad de Postman
 * ✅ Especificación OpenAPI 3.0
 * ✅ Ejemplos de request/response
 * ✅ Validaciones y códigos de estado HTTP
 *
 * @author GitHub Copilot
 * @version 1.0.0
 * @since 2025-11-24
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura la información principal de la API para Swagger UI.
     *
     * Define:
     * - Título y descripción de la API
     * - Versión actual
     * - Información de contacto
     * - Licencia del proyecto
     * - Servidores disponibles (local, producción)
     *
     * @return Objeto OpenAPI configurado con toda la información de la API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🧬 Mutant Detector API")
                        .version("1.0.0")
                        .description("""
                            # API REST para Detección de Mutantes
                            
                            Esta API permite identificar si un humano es mutante analizando su secuencia de ADN.
                            
                            ## 🎯 Reglas de Detección
                            
                            Un humano es **mutante** si su ADN contiene **más de una secuencia** de 4 letras 
                            iguales consecutivas en cualquiera de estas direcciones:
                            
                            - ➡️ **Horizontal** (izquierda a derecha)
                            - ⬇️ **Vertical** (arriba hacia abajo)
                            - ↘️ **Diagonal descendente** (de esquina superior izquierda a inferior derecha)
                            - ↗️ **Diagonal ascendente** (de esquina inferior izquierda a superior derecha)
                            
                            ## 📊 Endpoints Principales
                            
                            ### POST /mutant
                            Verifica si una secuencia de ADN corresponde a un mutante.
                            - **200 OK**: Es mutante
                            - **403 Forbidden**: No es mutante (humano normal)
                            - **400 Bad Request**: ADN inválido
                            
                            ### GET /stats
                            Obtiene estadísticas de todas las verificaciones realizadas.
                            - Retorna cantidad de mutantes detectados
                            - Retorna cantidad de humanos detectados
                            - Calcula el ratio mutantes/humanos
                            
                            ## 🔧 Tecnologías
                            
                            - **Spring Boot 3.2.0** - Framework principal
                            - **Java 17** - Lenguaje de programación
                            - **H2 Database** - Base de datos en memoria
                            - **JPA/Hibernate** - Persistencia de datos
                            - **Lombok** - Reducción de código boilerplate
                            - **JUnit 5** - Testing unitario e integración
                            - **Swagger/OpenAPI** - Documentación interactiva
                            
                            ## 📝 Ejemplo de Uso
                            
                            ```json
                            POST /mutant
                            {
                              "dna": [
                                "ATGCGA",
                                "CAGTGC",
                                "TTATGT",
                                "AGAAGG",
                                "CCCCTA",
                                "TCACTG"
                              ]
                            }
                            ```
                            
                            ## ⚡ Optimizaciones Implementadas
                            
                            - **Early Termination**: Se detiene al encontrar 2+ secuencias
                            - **Caché con Hash SHA-256**: Evita reprocesar el mismo ADN
                            - **Conversión a char[][]**: Acceso O(1) a la matriz
                            - **Boundary Checking**: Solo busca donde cabe una secuencia completa
                            - **Índices en BD**: Búsqueda O(log N) en base de datos
                            
                            ## 🚀 Deployment
                            
                            La API está desplegada en: **Render.com**
                            """)
                        .contact(new Contact()
                                .name("Proyecto Mutantes - MercadoLibre Challenge")
                                .url("https://github.com/tuusuario/mutantes")
                                .email("tu.email@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local de Desarrollo"),
                        new Server()
                                .url("https://tu-app.onrender.com")
                                .description("Servidor de Producción (Render)")
                ));
    }
}


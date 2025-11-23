package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error estandarizadas.
 * Se usa en el GlobalExceptionHandler para devolver errores consistentes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp del error.
     */
    private LocalDateTime timestamp;

    /**
     * Código de estado HTTP (400, 403, 500, etc.).
     */
    private int status;

    /**
     * Mensaje de error principal.
     */
    private String error;

    /**
     * Mensaje detallado del error.
     */
    private String message;

    /**
     * Ruta del endpoint donde ocurrió el error.
     */
    private String path;
}


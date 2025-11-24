package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.validation.ValidDnaSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir la secuencia de ADN en las peticiones HTTP.
 * Ejemplo de JSON esperado:
 * {
 *   "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar si un ADN es mutante")
public class DnaRequest {

    /**
     * Secuencia de ADN representada como array de strings.
     * Cada string representa una fila de la matriz NxN.
     *
     * Validaciones:
     * - No puede ser null
     * - No puede estar vacío
     * - Debe ser una matriz cuadrada (NxN)
     * - Solo puede contener A, T, C, G
     * - Tamaño mínimo 4x4
     */
    @Schema(
        description = "Secuencia de ADN representada como matriz NxN de strings. Cada string es una fila de la matriz y debe contener solo los caracteres A, T, C, G.",
        example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
        required = true
    )
    @NotNull(message = "La secuencia de ADN no puede ser nula")
    @NotEmpty(message = "La secuencia de ADN no puede estar vacía")
    @ValidDnaSequence
    private String[] dna;
}


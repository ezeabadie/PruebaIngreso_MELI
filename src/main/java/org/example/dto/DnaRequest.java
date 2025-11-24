package org.example.dto;

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
    @NotNull(message = "La secuencia de ADN no puede ser nula")
    @NotEmpty(message = "La secuencia de ADN no puede estar vacía")
    @ValidDnaSequence
    private String[] dna;
}


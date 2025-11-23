package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder con las estadísticas de verificación de ADN.
 * Ejemplo de JSON de respuesta:
 * {
 *   "count_mutant_dna": 40,
 *   "count_human_dna": 100,
 *   "ratio": 0.4
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {

    /**
     * Cantidad de ADN mutantes detectados.
     */
    private long countMutantDna;

    /**
     * Cantidad total de ADN humanos (mutantes + no mutantes).
     */
    private long countHumanDna;

    /**
     * Ratio de mutantes sobre el total.
     * Formula: countMutantDna / countHumanDna
     * Si no hay humanos verificados, retorna 0.0
     */
    private double ratio;

    /**
     * Constructor que calcula automáticamente el ratio.
     */
    public StatsResponse(long countMutantDna, long countHumanDna) {
        this.countMutantDna = countMutantDna;
        this.countHumanDna = countHumanDna;
        this.ratio = countHumanDna == 0 ? 0.0 : (double) countMutantDna / countHumanDna;
    }
}


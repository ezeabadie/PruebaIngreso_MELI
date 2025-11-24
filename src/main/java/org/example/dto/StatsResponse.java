package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Estadísticas de verificaciones de ADN realizadas")
public class StatsResponse {

    /**
     * Cantidad de ADN mutantes detectados.
     */
    @Schema(
        description = "Cantidad de ADN mutante verificado",
        example = "40"
    )
    @JsonProperty("count_mutant_dna")
    private long countMutantDna;

    /**
     * Cantidad total de ADN humanos (mutantes + no mutantes).
     */
    @Schema(
        description = "Cantidad de ADN humano verificado",
        example = "100"
    )
    @JsonProperty("count_human_dna")
    private long countHumanDna;

    /**
     * Ratio de mutantes sobre el total.
     * Formula: countMutantDna / countHumanDna
     * Si no hay humanos verificados, retorna 0.0
     */
    @Schema(
        description = "Ratio: mutantes / humanos",
        example = "0.4"
    )
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


package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DnaRequest;
import org.example.dto.ErrorResponse;
import org.example.dto.StatsResponse;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ========================================
 * CONTROLADOR REST - API DE MUTANTES
 * ========================================
 *
 * Endpoints:
 * - POST /mutant: Verifica si un ADN es mutante
 * - GET /stats: Obtiene estadísticas de verificaciones
 *
 * @author GitHub Copilot
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mutant Detection", description = "API para detección de mutantes basada en secuencias de ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    /**
     * ========================================
     * POST /mutant - Verificar si es mutante
     * ========================================
     *
     * Recibe una secuencia de ADN y determina si corresponde a un mutante.
     *
     * Request Body (JSON):
     * {
     *   "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
     * }
     *
     * Respuestas:
     * - 200 OK: Es mutante
     * - 403 Forbidden: No es mutante
     * - 400 Bad Request: ADN inválido
     *
     * @param request DTO con la secuencia de ADN
     * @return ResponseEntity vacío con código de estado correspondiente
     */
    @PostMapping("/mutant")
    @Operation(
        summary = "Verifica si un ADN es mutante",
        description = "Recibe una secuencia de ADN en formato de matriz NxN y determina si corresponde a un mutante. " +
                      "Un humano es mutante si tiene más de una secuencia de 4 letras iguales en dirección horizontal, " +
                      "vertical o diagonal."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es mutante",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No es mutante",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Secuencia de ADN inválida",
            content = @Content
        )
    })
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        log.info("POST /mutant - Verificando ADN");

        boolean isMutant = mutantService.isMutant(request.getDna());

        if (isMutant) {
            log.info("Resultado: ES MUTANTE - HTTP 200");
            return ResponseEntity.ok().build();  // 200 OK
        } else {
            log.info("Resultado: NO ES MUTANTE - HTTP 403");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // 403 Forbidden
        }
    }

    /**
     * ========================================
     * GET /stats - Obtener estadísticas
     * ========================================
     *
     * Retorna estadísticas de las verificaciones de ADN realizadas.
     *
     * Response Body (JSON):
     * {
     *   "count_mutant_dna": 40,
     *   "count_human_dna": 100,
     *   "ratio": 0.4
     * }
     *
     * @return ResponseEntity con las estadísticas
     */
    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Obtiene estadísticas de verificaciones",
        description = "Retorna la cantidad de ADN mutantes detectados, el total de verificaciones realizadas " +
                      "y el ratio de mutantes sobre el total."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = StatsResponse.class)
            )
        )
    })
    public ResponseEntity<StatsResponse> getStats() {
        log.info("GET /stats - Obteniendo estadísticas");

        StatsResponse stats = statsService.getStats();

        log.info("Estadísticas: Mutantes={}, Humanos={}, Ratio={}",
                 stats.getCountMutantDna(),
                 stats.getCountHumanDna(),
                 stats.getRatio());

        return ResponseEntity.ok(stats);
    }
}


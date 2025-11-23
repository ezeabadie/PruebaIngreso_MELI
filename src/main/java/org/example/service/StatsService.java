package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para obtener estadísticas de verificaciones de ADN.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final DnaRecordRepository repository;

    /**
     * Obtiene las estadísticas de verificaciones de ADN.
     *
     * Retorna:
     * - count_mutant_dna: Cantidad de ADN mutantes detectados
     * - count_human_dna: Cantidad total de ADN verificados (mutantes + humanos)
     * - ratio: Proporción de mutantes sobre el total
     *
     * Ejemplo de respuesta:
     * {
     *   "count_mutant_dna": 40,
     *   "count_human_dna": 100,
     *   "ratio": 0.4
     * }
     *
     * @return StatsResponse con las estadísticas
     */
    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        log.debug("Obteniendo estadísticas de verificaciones");

        long mutantCount = repository.countMutants();
        long humanCount = repository.countHumans();

        log.info("Estadísticas - Mutantes: {}, Humanos totales: {}", mutantCount, humanCount);

        return new StatsResponse(mutantCount, humanCount);
    }
}


package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DnaRecord;
import org.example.exception.DnaHashCalculationException;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Servicio principal para verificación de ADN mutante.
 *
 * Responsabilidades:
 * - Calcular hash SHA-256 del ADN para evitar duplicados
 * - Verificar si el ADN ya fue procesado anteriormente
 * - Delegar la detección al MutantDetector
 * - Guardar los resultados en la base de datos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository repository;

    /**
     * Verifica si un ADN corresponde a un mutante.
     *
     * Flujo:
     * 1. Calcular hash SHA-256 del ADN
     * 2. Verificar si ya existe en BD (evitar reprocesar)
     * 3. Si no existe, ejecutar algoritmo de detección
     * 4. Guardar resultado en BD
     * 5. Retornar resultado
     *
     * @param dna Array de strings representando el ADN
     * @return true si es mutante, false si no lo es
     */
    @Transactional
    public boolean isMutant(String[] dna) {
        log.debug("Verificando ADN: {}", Arrays.toString(dna));

        // Calcular hash del ADN para identificarlo de forma única
        String dnaHash = calculateDnaHash(dna);
        log.debug("Hash del ADN: {}", dnaHash);

        // Verificar si ya fue procesado anteriormente
        Optional<DnaRecord> existingRecord = repository.findByDnaHash(dnaHash);

        if (existingRecord.isPresent()) {
            log.info("ADN ya procesado anteriormente. Hash: {}, Es mutante: {}",
                     dnaHash, existingRecord.get().getIsMutant());
            return existingRecord.get().getIsMutant();
        }

        // Si no existe, ejecutar algoritmo de detección
        boolean isMutant = mutantDetector.isMutant(dna);
        log.info("Resultado de detección - Hash: {}, Es mutante: {}", dnaHash, isMutant);

        // Guardar resultado en base de datos
        DnaRecord record = new DnaRecord(dnaHash, isMutant);
        repository.save(record);
        log.debug("Registro guardado en BD: {}", record);

        return isMutant;
    }

    /**
     * Calcula el hash SHA-256 de una secuencia de ADN.
     *
     * ¿Por qué usar hash en lugar de guardar el ADN completo?
     * - Seguridad: No almacenamos datos sensibles
     * - Eficiencia: El hash ocupa siempre 64 caracteres vs arrays grandes
     * - Unicidad: SHA-256 garantiza identificación única
     *
     * Ejemplo:
     * Input: ["ATGCGA", "CAGTGC", ...]
     * Output: "a3f5c8e9b2d4..."  (hash de 64 caracteres hexadecimales)
     *
     * @param dna Array de strings representando el ADN
     * @return Hash SHA-256 en formato hexadecimal
     * @throws DnaHashCalculationException si falla el cálculo del hash
     */
    private String calculateDnaHash(String[] dna) {
        try {
            // Concatenar todas las filas en un solo string
            String concatenatedDna = String.join("", dna);

            // Obtener instancia del algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcular el hash
            byte[] hashBytes = digest.digest(concatenatedDna.getBytes(StandardCharsets.UTF_8));

            // Convertir bytes a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("Error calculando hash SHA-256 del ADN", e);
            throw new DnaHashCalculationException("Error al calcular hash del ADN", e);
        }
    }
}


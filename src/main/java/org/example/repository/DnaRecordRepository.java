package org.example.repository;

import org.example.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos con DnaRecord.
 */
@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Busca un registro por su hash SHA-256.
     * Útil para evitar procesar el mismo ADN dos veces.
     *
     * @param dnaHash Hash SHA-256 de la secuencia de ADN
     * @return Optional con el registro si existe
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Cuenta registros según si son mutantes o no.
     * Query method derivado - Spring Data JPA genera automáticamente:
     * SELECT COUNT(*) FROM dna_records WHERE is_mutant = ?
     *
     * @param isMutant true para contar mutantes, false para contar humanos
     * @return Cantidad de registros con el valor isMutant especificado
     */
    long countByIsMutant(boolean isMutant);

    /**
     * Cuenta cuántos ADN mutantes hay registrados.
     * Método alternativo usando @Query explícita.
     *
     * @return Cantidad de registros donde isMutant = true
     */
    @Query("SELECT COUNT(d) FROM DnaRecord d WHERE d.isMutant = true")
    long countMutants();

    /**
     * Cuenta cuántos ADN humanos (mutantes + no mutantes) hay registrados.
     * Método alternativo usando @Query explícita.
     *
     * @return Cantidad total de registros
     */
    @Query("SELECT COUNT(d) FROM DnaRecord d")
    long countHumans();
}


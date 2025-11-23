package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de verificación de ADN.
 * Se almacena en la base de datos H2.
 *
 * Tabla: dna_records
 */
@Entity
@Table(name = "dna_records", indexes = {
    @Index(name = "idx_dna_hash", columnList = "dna_hash", unique = true),
    @Index(name = "idx_is_mutant", columnList = "is_mutant")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnaRecord {

    /**
     * ID autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash SHA-256 de la secuencia de ADN.
     * Se usa para evitar duplicados sin almacenar el ADN completo.
     * Columna única para evitar procesar el mismo ADN dos veces.
     */
    @Column(name = "dna_hash", nullable = false, unique = true, length = 64)
    private String dnaHash;

    /**
     * Indica si el ADN corresponde a un mutante.
     */
    @Column(name = "is_mutant", nullable = false)
    private Boolean isMutant;

    /**
     * Timestamp de cuándo se verificó el ADN.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor para crear un nuevo registro.
     */
    public DnaRecord(String dnaHash, Boolean isMutant) {
        this.dnaHash = dnaHash;
        this.isMutant = isMutant;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}


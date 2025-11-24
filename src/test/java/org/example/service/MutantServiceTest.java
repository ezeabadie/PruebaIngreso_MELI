package org.example.service;

import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MutantService.
 * Total: 5 tests según consigna 3.2
 */
@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository repository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        humanDna = new String[]{
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAC"
        };
    }

    @Test
    @DisplayName("isMutant debe retornar true cuando el ADN es mutante")
    void testIsMutant_ReturnTrue_WhenDnaIsMutant() {
        // Arrange
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(repository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result);
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(repository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("isMutant debe retornar false cuando el ADN NO es mutante")
    void testIsMutant_ReturnFalse_WhenDnaIsNotMutant() {
        // Arrange
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(repository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.isMutant(humanDna);

        // Assert
        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(repository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("isMutant NO debe ejecutar algoritmo si ADN ya existe en BD")
    void testIsMutant_ShouldNotExecuteAlgorithm_WhenDnaAlreadyExists() {
        // Arrange
        DnaRecord existingRecord = new DnaRecord();
        existingRecord.setIsMutant(true);
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result);
        verify(mutantDetector, never()).isMutant(any());  // ← NO ejecuta algoritmo
        verify(repository, never()).save(any());           // ← NO guarda (ya existe)
    }

    @Test
    @DisplayName("isMutant debe guardar resultado en BD cuando es nuevo ADN")
    void testIsMutant_ShouldSaveResult_WhenDnaIsNew() {
        // Arrange
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(repository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        mutantService.isMutant(mutantDna);

        // Assert
        verify(repository, times(1)).save(argThat(record ->
            record.getIsMutant() == true &&
            record.getDnaHash() != null
        ));
    }

    @Test
    @DisplayName("isMutant debe propagar IllegalArgumentException del detector")
    void testIsMutant_ShouldPropagateException_FromDetector() {
        // Arrange: usar DNA inválido (matriz no cuadrada)
        String[] invalidDna = {"ATGC", "CAG"}; // 4x2 no es válido

        when(mutantDetector.isMutant(invalidDna))
            .thenThrow(new IllegalArgumentException("La matriz debe ser NxN"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            mutantService.isMutant(invalidDna);
        });

        verify(mutantDetector).isMutant(invalidDna);
        verify(repository, never()).save(any());
    }
}


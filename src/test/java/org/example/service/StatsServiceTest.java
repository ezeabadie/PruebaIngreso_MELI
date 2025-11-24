package org.example.service;

import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para StatsService.
 * Total: 6 tests según consigna 3.2
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository repository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("getStats debe retornar estadísticas correctas")
    void testGetStats_ReturnCorrectStats() {
        // Arrange
        when(repository.countMutants()).thenReturn(40L);
        when(repository.countHumans()).thenReturn(100L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(40, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("getStats debe calcular ratio 0 cuando no hay humanos")
    void testGetStats_ReturnZeroRatio_WhenNoHumans() {
        // Arrange
        when(repository.countMutants()).thenReturn(0L);
        when(repository.countHumans()).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(0, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio());
    }

    @Test
    @DisplayName("getStats debe calcular ratio 1.0 cuando todos son mutantes")
    void testGetStats_ReturnRatioOne_WhenAllAreMutants() {
        // Arrange
        when(repository.countMutants()).thenReturn(50L);
        when(repository.countHumans()).thenReturn(50L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(50, stats.getCountMutantDna());
        assertEquals(50, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("getStats debe manejar grandes cantidades de datos")
    void testGetStats_HandleLargeNumbers() {
        // Arrange
        when(repository.countMutants()).thenReturn(1_000_000L);
        when(repository.countHumans()).thenReturn(5_000_000L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(1_000_000, stats.getCountMutantDna());
        assertEquals(5_000_000, stats.getCountHumanDna());
        assertEquals(0.2, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("getStats debe invocar repository solo una vez por métrica")
    void testGetStats_InvokeRepositoryOnce() {
        // Arrange
        when(repository.countMutants()).thenReturn(10L);
        when(repository.countHumans()).thenReturn(20L);

        // Act
        statsService.getStats();

        // Assert
        verify(repository, times(1)).countMutants();
        verify(repository, times(1)).countHumans();
    }

    @Test
    @DisplayName("getStats debe retornar valores exactos sin redondeo")
    void testGetStats_ReturnsExactValues() {
        // Arrange
        when(repository.countMutants()).thenReturn(3L);
        when(repository.countHumans()).thenReturn(7L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(3, stats.getCountMutantDna());
        assertEquals(7, stats.getCountHumanDna());
        assertEquals(0.42857142857142855, stats.getRatio(), 0.0001);
    }
}


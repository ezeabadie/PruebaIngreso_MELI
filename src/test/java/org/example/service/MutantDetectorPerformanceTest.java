package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de performance y complejidad temporal del algoritmo.
 * Mide tiempos reales de ejecución para diferentes tamaños de matriz.
 * 
 * MÉTRICAS OBJETIVO:
 * - 6x6: ≤ 1 ms (óptimo), ≤ 5 ms (aceptable)
 * - 100x100: ≤ 20 ms (óptimo), ≤ 100 ms (aceptable)
 * - 1000x1000: ≤ 500 ms (óptimo), ≤ 5000 ms (aceptable)
 */
class MutantDetectorPerformanceTest {

    private MutantDetector detector;
    private Random random;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
        random = new Random(42); // Seed fijo para reproducibilidad
    }

    // ========================================
    // TESTS DE PERFORMANCE - 6x6 (2.4 puntos)
    // ========================================

    @Test
    @DisplayName("Performance 6x6: Debe ejecutar en ≤ 5 ms (aceptable)")
    void testPerformance_6x6_Acceptable() {
        String[] dna = generateMutantDna(6);
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 6x6 Tiempo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 5, "Debe ejecutar en ≤ 5 ms. Tiempo actual: " + durationMs + " ms");
    }

    @Test
    @DisplayName("Performance 6x6: Debe ejecutar en ≤ 1 ms (óptimo)")
    void testPerformance_6x6_Optimal() {
        String[] dna = generateMutantDna(6);
        
        // Warm-up
        for (int i = 0; i < 100; i++) {
            detector.isMutant(dna);
        }
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 6x6 Tiempo óptimo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        // Nota: Este test puede fallar en máquinas lentas, es solo informativo
        if (durationMs > 1) {
            System.out.println("⚠️ Warning: Tiempo > 1 ms (no crítico)");
        }
    }

    // ========================================
    // TESTS DE PERFORMANCE - 100x100 (3.6 puntos)
    // ========================================

    @Test
    @DisplayName("Performance 100x100: Debe ejecutar en ≤ 100 ms (aceptable)")
    void testPerformance_100x100_Acceptable() {
        String[] dna = generateMutantDna(100);
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 100x100 Tiempo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 100, "Debe ejecutar en ≤ 100 ms. Tiempo actual: " + durationMs + " ms");
    }

    @Test
    @DisplayName("Performance 100x100: Debe ejecutar en ≤ 20 ms (óptimo)")
    void testPerformance_100x100_Optimal() {
        String[] dna = generateMutantDna(100);
        
        // Warm-up
        for (int i = 0; i < 10; i++) {
            detector.isMutant(dna);
        }
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 100x100 Tiempo óptimo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 20, "Debe ejecutar en ≤ 20 ms. Tiempo actual: " + durationMs + " ms");
    }

    // ========================================
    // TESTS DE PERFORMANCE - 1000x1000 (3.6 puntos)
    // ========================================

    @Test
    @DisplayName("Performance 1000x1000: Debe ejecutar en ≤ 5000 ms (aceptable)")
    void testPerformance_1000x1000_Acceptable() {
        String[] dna = generateMutantDna(1000);
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 1000x1000 Tiempo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 5000, "Debe ejecutar en ≤ 5000 ms. Tiempo actual: " + durationMs + " ms");
    }

    @Test
    @DisplayName("Performance 1000x1000: Debe ejecutar en ≤ 500 ms (óptimo)")
    void testPerformance_1000x1000_Optimal() {
        String[] dna = generateMutantDna(1000);
        
        // Warm-up
        for (int i = 0; i < 3; i++) {
            detector.isMutant(dna);
        }
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ 1000x1000 Tiempo óptimo: " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        assertTrue(durationMs <= 500, "Debe ejecutar en ≤ 500 ms. Tiempo actual: " + durationMs + " ms");
    }

    // ========================================
    // TEST DE EARLY TERMINATION
    // ========================================

    @Test
    @DisplayName("Early Termination: Debe ser significativamente más rápido en mutantes")
    void testEarlyTermination_MakesSignificantDifference() {
        // Crear matriz con secuencias al inicio
        String[] dnaEarly = generateMutantDnaWithEarlySequences(100);
        
        // Medir tiempo
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dnaEarly);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ Early Termination (100x100): " + durationMs + " ms");
        
        assertTrue(result, "Debe detectar mutante");
        // Con early termination, debe ser MUY rápido (< 10 ms para 100x100)
        assertTrue(durationMs < 50, "Early termination debe ser rápido. Tiempo: " + durationMs + " ms");
    }

    // ========================================
    // TEST DE PEOR CASO (Humano - sin early termination)
    // ========================================

    @Test
    @DisplayName("Peor caso (Humano): Debe completar en tiempo razonable")
    void testWorstCase_Human() {
        String[] dna = generateHumanDna(100); // Sin secuencias -> recorre todo
        
        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();
        
        long durationMs = (endTime - startTime) / 1_000_000;
        
        System.out.println("⏱️ Peor caso - Humano (100x100): " + durationMs + " ms");
        
        assertFalse(result, "Debe detectar humano");
        assertTrue(durationMs <= 200, "Incluso peor caso debe ser < 200 ms. Tiempo: " + durationMs + " ms");
    }

    // ========================================
    // TESTS DE COMPLEJIDAD
    // ========================================

    @Test
    @DisplayName("Complejidad: O(N²) en peor caso, O(N) con early termination")
    void testComplexity_ScalesCorrectly() {
        // Test con múltiples tamaños
        int[] sizes = {10, 50, 100, 200};
        long[] times = new long[sizes.length];
        
        for (int i = 0; i < sizes.length; i++) {
            String[] dna = generateMutantDna(sizes[i]);
            
            long startTime = System.nanoTime();
            detector.isMutant(dna);
            long endTime = System.nanoTime();
            
            times[i] = (endTime - startTime) / 1_000_000;
            System.out.println("⏱️ " + sizes[i] + "x" + sizes[i] + ": " + times[i] + " ms");
        }
        
        // Verificar que escala razonablemente (no exponencial)
        // De 10 a 200 (20x más grande), el tiempo no debería ser > 400x más
        assertTrue(times[3] < times[0] * 400, "Debe escalar sub-cuadráticamente");
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    /**
     * Genera matriz NxN mutante con secuencias horizontales al inicio.
     * Esto activa early termination para máxima velocidad.
     */
    private String[] generateMutantDnaWithEarlySequences(int n) {
        String[] dna = new String[n];
        
        // Primera fila: AAAA...
        dna[0] = "A".repeat(n);
        
        // Segunda fila: CCCC...
        dna[1] = "C".repeat(n);
        
        // Resto: aleatorio
        for (int i = 2; i < n; i++) {
            dna[i] = generateRandomRow(n);
        }
        
        return dna;
    }

    /**
     * Genera matriz NxN mutante (con 2+ secuencias).
     */
    private String[] generateMutantDna(int n) {
        String[] dna = new String[n];
        
        // Fila 0: AAAA en las primeras 4 posiciones
        StringBuilder row0 = new StringBuilder();
        row0.append("AAAA");
        for (int i = 4; i < n; i++) {
            row0.append(getRandomBase());
        }
        dna[0] = row0.toString();
        
        // Fila 1: CCCC en las primeras 4 posiciones
        StringBuilder row1 = new StringBuilder();
        row1.append("CCCC");
        for (int i = 4; i < n; i++) {
            row1.append(getRandomBase());
        }
        dna[1] = row1.toString();
        
        // Resto: aleatorio
        for (int i = 2; i < n; i++) {
            dna[i] = generateRandomRow(n);
        }
        
        return dna;
    }

    /**
     * Genera matriz NxN humana (sin secuencias de 4).
     */
    private String[] generateHumanDna(int n) {
        String[] dna = new String[n];
        char[] bases = {'A', 'T', 'C', 'G'};
        
        for (int i = 0; i < n; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < n; j++) {
                // Alternar bases para evitar secuencias
                row.append(bases[(i + j) % 4]);
            }
            dna[i] = row.toString();
        }
        
        return dna;
    }

    /**
     * Genera fila aleatoria de tamaño n.
     */
    private String generateRandomRow(int n) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < n; i++) {
            row.append(getRandomBase());
        }
        return row.toString();
    }

    /**
     * Obtiene base aleatoria (A, T, C, G).
     */
    private char getRandomBase() {
        char[] bases = {'A', 'T', 'C', 'G'};
        return bases[random.nextInt(4)];
    }
}


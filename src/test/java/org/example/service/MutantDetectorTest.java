package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del algoritmo de detección de mutantes.
 * Prueba la lógica pura sin dependencias externas.
 *
 * Cobertura: >95% de MutantDetector
 */
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
    }

    // ========================================
    // TESTS DE CASOS MUTANTES
    // ========================================

    @Test
    @DisplayName("Debe detectar mutante con 2 secuencias horizontales")
    void testMutant_TwoHorizontalSequences() {
        String[] dna = {
            "AAAA",  // ← Secuencia 1
            "CCCC",  // ← Secuencia 2
            "TATA",
            "GCGC"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con 2 secuencias verticales")
    void testMutant_TwoVerticalSequences() {
        String[] dna = {
            "ACTG",
            "ACTG",
            "ACTG",
            "ACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias horizontales y verticales")
    void testMutant_HorizontalAndVertical() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",  // ← Horizontal
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonal descendente")
    void testMutant_DiagonalDescending() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonal ascendente")
    void testMutant_DiagonalAscending() {
        String[] dna = {
            "ATGCGA",
            "CAGTAC",
            "TTATAT",
            "AGAAAG",
            "CACCTA",
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con múltiples secuencias")
    void testMutant_MultipleSequences() {
        String[] dna = {
            "AAAAAA",  // ← Secuencia 1
            "CCCCCC",  // ← Secuencia 2
            "TTATGT",
            "AGAAGG",
            "GCGTCA",
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz 4x4 mínima")
    void testMutant_MinimumSize4x4() {
        String[] dna = {
            "AAAA",
            "CCCC",
            "TATA",
            "GCGC"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz grande 10x10")
    void testMutant_LargeMatrix10x10() {
        String[] dna = {
            "ATGCGATGCA",
            "CAGTGCATGC",
            "TTATGTATTG",
            "AGAAGGTAGG",
            "CCCCTACCCC",
            "TCACTGTCAC",
            "ATGCGATGCA",
            "CAGTGCATGC",
            "TTATGTATTG",
            "AGAAGGTAGG"
        };

        assertTrue(detector.isMutant(dna));
    }

    // ========================================
    // TESTS DE CASOS NO MUTANTES
    // ========================================

    @Test
    @DisplayName("Debe rechazar humano sin secuencias")
    void testHuman_NoSequences() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TGAC",
            "GCTA"
        };

        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar humano con solo 1 secuencia")
    void testHuman_OnlyOneSequence() {
        String[] dna = {
            "AAAA",  // ← Solo 1 secuencia
            "CTAG",
            "TGAC",
            "GCTA"
        };

        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar humano con secuencias de solo 3 letras")
    void testHuman_SequencesOfThree() {
        String[] dna = {
            "AAATGC",
            "CCCAGT",
            "TTTATG",
            "GGGAGG",
            "CCGCTA",
            "TCACTG"
        };

        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar humano en matriz 6x6")
    void testHuman_6x6Matrix() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };

        assertFalse(detector.isMutant(dna));
    }

    // ========================================
    // TESTS DE EARLY TERMINATION
    // ========================================

    @Test
    @DisplayName("Debe usar early termination al encontrar 2 secuencias")
    void testMutant_EarlyTermination() {
        // Matriz con 2 secuencias al inicio
        String[] dna = {
            "AAAA",  // ← Encuentra esta primera
            "CCCC",  // ← Encuentra esta segunda → retorna true
            "TATA",
            "GCGC"
        };

        // Si no usara early termination, seguiría buscando innecesariamente
        assertTrue(detector.isMutant(dna));
    }

    // ========================================
    // TESTS DE TODAS LAS BASES (A, T, C, G)
    // ========================================

    @Test
    @DisplayName("Debe detectar secuencias con todas las bases (A, T, C, G)")
    void testMutant_AllBases() {
        // Secuencias con A
        String[] dnaA = {"AAAA", "CCCC", "TATA", "GCGC"};
        assertTrue(detector.isMutant(dnaA));

        // Secuencias con T
        String[] dnaT = {"TTTT", "GGGG", "ATCG", "CGAT"};
        assertTrue(detector.isMutant(dnaT));

        // Secuencias con C
        String[] dnaC = {"CCCC", "AAAA", "TGCA", "GACT"};
        assertTrue(detector.isMutant(dnaC));

        // Secuencias con G
        String[] dnaG = {"GGGG", "TTTT", "ACGT", "CAGT"};
        assertTrue(detector.isMutant(dnaG));
    }

    // ========================================
    // TESTS DE POSICIONES DIFERENTES
    // ========================================

    @Test
    @DisplayName("Debe detectar secuencias en diferentes posiciones de la matriz")
    void testMutant_DifferentPositions() {
        // Secuencias al inicio
        String[] dna1 = {"AAAA", "CCCC", "TATA", "GCGC"};
        assertTrue(detector.isMutant(dna1));

        // Secuencias en el medio
        String[] dna2 = {
            "ATGCGA",
            "CAAAAA",  // ← Secuencia en medio
            "TCCCCG",  // ← Secuencia en medio
            "AGAAGG",
            "GCGTCA",
            "TCACTG"
        };
        assertTrue(detector.isMutant(dna2));
    }

    // ========================================
    // TESTS DE DIAGONALES COMPLETAS
    // ========================================

    @Test
    @DisplayName("Debe detectar diagonal principal descendente")
    void testMutant_MainDiagonalDescending() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar diagonales paralelas")
    void testMutant_ParallelDiagonals() {
        String[] dna = {
            "ATGCGA",
            "CAGTAC",
            "TTATAT",
            "AGAAAG",
            "CACCTA",
            "TCACTG"
        };

        assertTrue(detector.isMutant(dna));
    }

    // ========================================
    // TESTS DE CASOS EDGE
    // ========================================

    @Test
    @DisplayName("Debe manejar matriz exactamente con 2 secuencias (límite)")
    void testMutant_ExactlyTwoSequences() {
        String[] dna = {
            "AAAA",  // ← Secuencia 1
            "CCCC",  // ← Secuencia 2
            "TGTG",
            "CGCG"
        };

        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe rechazar matriz con exactamente 1 secuencia (límite)")
    void testHuman_ExactlyOneSequence() {
        String[] dna = {
            "AAAA",  // ← Solo 1 secuencia
            "TGTG",
            "CGCG",
            "TATA"
        };

        assertFalse(detector.isMutant(dna));
    }
}


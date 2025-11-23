package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de validación del input para MutantDetector.
 * Verifica que todas las validaciones requeridas funcionen correctamente.
 */
class MutantDetectorValidationTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
    }

    // ========================================
    // VALIDACIÓN 1: Array no nulo o vacío
    // ========================================

    @Test
    @DisplayName("Debe lanzar excepción si el ADN es nulo")
    void testNullDna() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(null)
        );

        assertEquals("El ADN no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ADN está vacío")
    void testEmptyDna() {
        String[] emptyDna = {};

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(emptyDna)
        );

        assertEquals("El ADN no puede ser nulo o vacío", exception.getMessage());
    }

    // ========================================
    // VALIDACIÓN 2: Tamaño mínimo 4x4
    // ========================================

    @Test
    @DisplayName("Debe lanzar excepción si la matriz es menor a 4x4")
    void testMatrixTooSmall() {
        String[] smallDna = {
            "ATG",
            "CAG",
            "TTA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(smallDna)
        );

        assertTrue(exception.getMessage().contains("debe tener al menos 4x4"));
    }

    // ========================================
    // VALIDACIÓN 3: Matriz NxN
    // ========================================

    @Test
    @DisplayName("Debe lanzar excepción si una fila es nula")
    void testNullRow() {
        String[] dnaWithNullRow = {
            "ATGC",
            null,
            "TTAT",
            "AGAA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithNullRow)
        );

        assertTrue(exception.getMessage().contains("no puede ser nula"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la matriz no es cuadrada (NxN)")
    void testNonSquareMatrix() {
        String[] nonSquareDna = {
            "ATGCGA",
            "CAGT",      // ← Fila más corta
            "TTATGT",
            "AGAAGG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(nonSquareDna)
        );

        assertTrue(exception.getMessage().contains("matriz debe ser NxN"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si una fila es más larga")
    void testRowTooLong() {
        String[] dnaWithLongRow = {
            "ATGC",
            "CAGTGC",    // ← Fila más larga
            "TTAT",
            "AGAA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithLongRow)
        );

        assertTrue(exception.getMessage().contains("debe tener 4"));
    }

    // ========================================
    // VALIDACIÓN 4: Solo caracteres A, T, C, G
    // ========================================

    @Test
    @DisplayName("Debe lanzar excepción si hay caracteres inválidos (minúsculas)")
    void testInvalidCharactersLowercase() {
        String[] dnaWithLowercase = {
            "ATGCga",    // ← 'g', 'a' minúsculas
            "CAGTGC",
            "TTATGT",
            "AGAAGG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithLowercase)
        );

        assertTrue(exception.getMessage().contains("Carácter inválido"));
        assertTrue(exception.getMessage().contains("Solo se permiten: A, T, C, G"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si hay caracteres no permitidos (X, Y, Z)")
    void testInvalidCharactersXYZ() {
        String[] dnaWithInvalidChars = {
            "ATGCGA",
            "CXYZGC",    // ← X, Y, Z inválidos
            "TTATGT",
            "AGAAGG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithInvalidChars)
        );

        assertTrue(exception.getMessage().contains("Carácter inválido 'X'"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si hay números")
    void testInvalidCharactersNumbers() {
        String[] dnaWithNumbers = {
            "ATGC",
            "C123",      // ← Números
            "TTAT",
            "AGAA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithNumbers)
        );

        assertTrue(exception.getMessage().contains("Carácter inválido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si hay caracteres especiales")
    void testInvalidCharactersSpecial() {
        String[] dnaWithSpecialChars = {
            "ATGC",
            "CA-T",      // ← Guion
            "TT@T",      // ← Arroba
            "AGAA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dnaWithSpecialChars)
        );

        assertTrue(exception.getMessage().contains("Carácter inválido"));
    }

    // ========================================
    // VALIDACIONES COMBINADAS
    // ========================================

    @Test
    @DisplayName("Debe validar correctamente un ADN válido de 4x4")
    void testValidDna4x4() {
        String[] validDna = {
            "AAAA",
            "CCCC",
            "TATA",
            "GCGC"
        };

        // No debe lanzar excepción
        assertDoesNotThrow(() -> {
            boolean result = detector.isMutant(validDna);
            assertTrue(result); // Es mutante (2 secuencias horizontales)
        });
    }

    @Test
    @DisplayName("Debe validar correctamente un ADN válido de 6x6")
    void testValidDna6x6() {
        String[] validDna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        // No debe lanzar excepción
        assertDoesNotThrow(() -> {
            boolean result = detector.isMutant(validDna);
            assertTrue(result); // Es mutante
        });
    }

    @Test
    @DisplayName("Debe validar correctamente un ADN humano (no mutante)")
    void testValidHumanDna() {
        String[] humanDna = {
            "ATGC",
            "CAGT",
            "TTAT",
            "AGAC"
        };

        // No debe lanzar excepción
        assertDoesNotThrow(() -> {
            boolean result = detector.isMutant(humanDna);
            assertFalse(result); // No es mutante
        });
    }

    @Test
    @DisplayName("Debe validar correctamente un ADN grande (10x10)")
    void testValidLargeDna() {
        String[] largeDna = {
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

        // No debe lanzar excepción
        assertDoesNotThrow(() -> {
            detector.isMutant(largeDna);
        });
    }

    // ========================================
    // MENSAJES DE ERROR DETALLADOS
    // ========================================

    @Test
    @DisplayName("El mensaje de error debe indicar la posición del carácter inválido")
    void testErrorMessageShowsPosition() {
        String[] dna = {
            "ATGC",
            "CAGT",
            "TTXT",    // ← X en posición [2][2]
            "AGAA"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna)
        );

        String message = exception.getMessage();
        assertTrue(message.contains("posición [2][2]"));
        assertTrue(message.contains("'X'"));
    }

    @Test
    @DisplayName("El mensaje de error debe mostrar la longitud esperada vs actual")
    void testErrorMessageShowsExpectedLength() {
        String[] dna = {
            "ATGCGA",    // 6 caracteres
            "CAGT",      // 4 caracteres ← Error
            "TTATGT",
            "AGAAGG"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> detector.isMutant(dna)
        );

        String message = exception.getMessage();
        assertTrue(message.contains("tiene longitud 4"));
        assertTrue(message.contains("debe tener 6"));
    }
}


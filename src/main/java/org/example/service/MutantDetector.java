package org.example.service;

import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * ========================================
 * DETECTOR DE MUTANTES - ALGORITMO CORE
 * ========================================
 *
 * Este componente implementa el algoritmo de detección de mutantes
 * según la especificación de Magneto.
 *
 * REGLAS:
 * - Un humano es mutante si tiene MÁS DE UNA secuencia de 4 letras iguales
 * - Las secuencias pueden estar en 4 direcciones: horizontal, vertical, diagonal ↘, diagonal ↗
 * - La matriz debe ser NxN con valores A, T, C, G
 *
 * OPTIMIZACIONES IMPLEMENTADAS:
 * 1. Early termination: Apenas encuentra 2 secuencias, retorna true
 * 2. Single pass: Un solo recorrido de la matriz
 * 3. Boundary checking: Verifica límites antes de buscar
 * 4. Direct comparison: Sin loops en métodos check
 * 5. Validation Set O(1): Set inmutable para validación rápida
 * 6. Conversión a char[][]: Acceso más rápido que String.charAt()
 *
 * COMPLEJIDAD:
 * - Temporal: O(N²) en peor caso, O(N) en mejor caso
 * - Espacial: O(N²) para char[][], O(1) adicional
 *
 * @author GitHub Copilot
 */
@Component
public class MutantDetector {

    /**
     * Longitud de secuencia requerida para considerar mutante.
     */
    private static final int SEQUENCE_LENGTH = 4;

    /**
     * Número mínimo de secuencias para ser considerado mutante.
     */
    private static final int MIN_SEQUENCES_FOR_MUTANT = 2;

    /**
     * Set inmutable de bases válidas para validación O(1).
     * Optimización #5: Validation Set O(1)
     */
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Determina si un ADN corresponde a un mutante.
     *
     * @param dna Array de strings representando la matriz NxN de ADN
     * @return true si es mutante (2+ secuencias), false si no lo es
     * @throws IllegalArgumentException si el ADN no es válido
     */
    public boolean isMutant(String[] dna) {
        // ========================================
        // VALIDACIÓN 1: Array no nulo o vacío
        // ========================================
        if (dna == null || dna.length == 0) {
            throw new IllegalArgumentException("El ADN no puede ser nulo o vacío");
        }

        final int n = dna.length;

        // ========================================
        // VALIDACIÓN 2: Tamaño mínimo 4x4
        // ========================================
        if (n < SEQUENCE_LENGTH) {
            throw new IllegalArgumentException(
                "El ADN debe tener al menos " + SEQUENCE_LENGTH + "x" + SEQUENCE_LENGTH +
                " (tamaño actual: " + n + "x" + n + ")"
            );
        }

        // ========================================
        // VALIDACIÓN 3 y 4: Matriz NxN y solo A, T, C, G
        // ========================================
        for (int i = 0; i < n; i++) {
            String row = dna[i];

            // Validar que la fila no sea nula
            if (row == null) {
                throw new IllegalArgumentException("La fila " + i + " no puede ser nula");
            }

            // Validar que todas las cadenas tengan la misma longitud (matriz NxN)
            if (row.length() != n) {
                throw new IllegalArgumentException(
                    "La fila " + i + " tiene longitud " + row.length() +
                    " pero debe tener " + n + " (matriz debe ser NxN)"
                );
            }

            // Validar que cada carácter sea A, T, C, o G (Optimización #5: Set O(1))
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (!VALID_BASES.contains(c)) {
                    throw new IllegalArgumentException(
                        "Carácter inválido '" + c + "' en posición [" + i + "][" + j + "]. " +
                        "Solo se permiten: A, T, C, G"
                    );
                }
            }
        }

        // ========================================
        // CONVERSIÓN A MATRIZ DE CARACTERES
        // ========================================
        // Convertir a char[][] para acceso más rápido O(1) vs String.charAt() O(N)
        char[][] matrix = new char[n][];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        // Buscar secuencias en las 4 direcciones
        int sequenceCount = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                // ========================================
                // BÚSQUEDA HORIZONTAL (→)
                // ========================================
                // Solo buscar si hay espacio para 4 caracteres
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkHorizontal(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount >= MIN_SEQUENCES_FOR_MUTANT) {
                            return true;  // ← EARLY TERMINATION
                        }
                    }
                }

                // ========================================
                // BÚSQUEDA VERTICAL (↓)
                // ========================================
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkVertical(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount >= MIN_SEQUENCES_FOR_MUTANT) {
                            return true;  // ← EARLY TERMINATION
                        }
                    }
                }

                // ========================================
                // BÚSQUEDA DIAGONAL DESCENDENTE (↘)
                // ========================================
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalDescending(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount >= MIN_SEQUENCES_FOR_MUTANT) {
                            return true;  // ← EARLY TERMINATION
                        }
                    }
                }

                // ========================================
                // BÚSQUEDA DIAGONAL ASCENDENTE (↗)
                // ========================================
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkDiagonalAscending(matrix, row, col)) {
                        sequenceCount++;
                        if (sequenceCount >= MIN_SEQUENCES_FOR_MUTANT) {
                            return true;  // ← EARLY TERMINATION
                        }
                    }
                }
            }
        }

        // Si llegamos aquí, encontró 0 o 1 secuencia → NO es mutante
        return false;
    }

    /**
     * Verifica si hay una secuencia horizontal de 4 letras iguales.
     *
     * Ejemplo:
     * [C][C][C][C] T A  ← 4 C's consecutivas
     *  ↑  ↑  ↑  ↑
     *  Revisa estas 4 posiciones
     *
     * @param matrix Matriz de caracteres
     * @param row Fila donde iniciar
     * @param col Columna donde iniciar
     * @return true si las 4 siguientes posiciones horizontales tienen el mismo carácter
     */
    private boolean checkHorizontal(char[][] matrix, int row, int col) {
        final char base = matrix[row][col];
        return matrix[row][col + 1] == base &&
               matrix[row][col + 2] == base &&
               matrix[row][col + 3] == base;
    }

    /**
     * Verifica si hay una secuencia vertical de 4 letras iguales.
     *
     * Ejemplo:
     * [A] T G C    ← row
     * [A] C G T    ← row+1
     * [A] T A T    ← row+2
     * [A] G A A    ← row+3
     *  ↑
     *  col: 4 A's verticales
     *
     * @param matrix Matriz de caracteres
     * @param row Fila donde iniciar
     * @param col Columna donde iniciar
     * @return true si las 4 siguientes posiciones verticales tienen el mismo carácter
     */
    private boolean checkVertical(char[][] matrix, int row, int col) {
        final char base = matrix[row][col];
        return matrix[row + 1][col] == base &&
               matrix[row + 2][col] == base &&
               matrix[row + 3][col] == base;
    }

    /**
     * Verifica si hay una secuencia diagonal descendente (↘) de 4 letras iguales.
     *
     * Ejemplo:
     *     0   1   2   3
     *   ┌───┬───┬───┬───┐
     * 0 │[A]│ T │ G │ C │
     *   ├───┼───┼───┼───┤
     * 1 │ C │[A]│ G │ T │
     *   ├───┼───┼───┼───┤
     * 2 │ T │ T │[A]│ T │
     *   ├───┼───┼───┼───┤
     * 3 │ A │ G │ A │[A]│
     *   └───┴───┴───┴───┘
     *
     * (0,0) → (1,1) → (2,2) → (3,3)
     *
     * @param matrix Matriz de caracteres
     * @param row Fila donde iniciar
     * @param col Columna donde iniciar
     * @return true si las 4 siguientes posiciones diagonales ↘ tienen el mismo carácter
     */
    private boolean checkDiagonalDescending(char[][] matrix, int row, int col) {
        final char base = matrix[row][col];
        return matrix[row + 1][col + 1] == base &&
               matrix[row + 2][col + 2] == base &&
               matrix[row + 3][col + 3] == base;
    }

    /**
     * Verifica si hay una secuencia diagonal ascendente (↗) de 4 letras iguales.
     *
     * Ejemplo:
     *     0   1   2   3
     *   ┌───┬───┬───┬───┐
     * 0 │ A │ T │ G │[C]│
     *   ├───┼───┼───┼───┤
     * 1 │ C │ A │[C]│ T │
     *   ├───┼───┼───┼───┤
     * 2 │ T │[C]│ A │ T │
     *   ├───┼───┼───┼───┤
     * 3 │[C]│ A │ A │ A │
     *   └───┴───┴───┴───┘
     *
     * (3,0) → (2,1) → (1,2) → (0,3)
     *
     * @param matrix Matriz de caracteres
     * @param row Fila donde iniciar (debe ser >= 3)
     * @param col Columna donde iniciar
     * @return true si las 4 siguientes posiciones diagonales ↗ tienen el mismo carácter
     */
    private boolean checkDiagonalAscending(char[][] matrix, int row, int col) {
        final char base = matrix[row][col];
        return matrix[row - 1][col + 1] == base &&
               matrix[row - 2][col + 2] == base &&
               matrix[row - 3][col + 3] == base;
    }
}


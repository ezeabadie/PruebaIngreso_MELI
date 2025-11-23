package org.example;

import org.example.service.MutantDetector;

/**
 * Clase de prueba manual para verificar el algoritmo de detección de mutantes.
 */
public class TestMutantDetector {

    public static void main(String[] args) {
        MutantDetector detector = new MutantDetector();

        System.out.println("========================================");
        System.out.println("PRUEBAS DEL DETECTOR DE MUTANTES");
        System.out.println("========================================\n");

        // Test 1: ES MUTANTE (2 secuencias)
        String[] mutant1 = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",  // ← Horizontal: C-C-C-C
            "TCACTG"
        };
        testDna("Test 1 - Mutante con horizontal", mutant1, detector, true);

        // Test 2: NO ES MUTANTE (solo 1 secuencia)
        String[] human1 = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",  // ← Solo una: T-T-T-T
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        testDna("Test 2 - Humano (1 secuencia)", human1, detector, false);

        // Test 3: ES MUTANTE (vertical + diagonal)
        String[] mutant2 = {
            "AAAAGA",  // ← Horizontal: A-A-A-A
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",  // ← Horizontal: C-C-C-C
            "TCACTG"
        };
        testDna("Test 3 - Mutante (2 horizontales)", mutant2, detector, true);

        // Test 4: ES MUTANTE (diagonal)
        String[] mutant3 = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        testDna("Test 4 - Ejemplo del enunciado", mutant3, detector, true);

        // Test 5: Matriz 4x4 mínima - ES MUTANTE
        String[] mutant4 = {
            "AAAA",  // ← Horizontal: A-A-A-A
            "CCCC",  // ← Horizontal: C-C-C-C
            "TATA",
            "GCGC"
        };
        testDna("Test 5 - Matriz 4x4 mutante", mutant4, detector, true);

        // Test 6: Matriz 4x4 - NO ES MUTANTE
        String[] human2 = {
            "AAAA",  // ← Solo una: A-A-A-A
            "CTAG",
            "TGAC",
            "GCTA"
        };
        testDna("Test 6 - Matriz 4x4 humano", human2, detector, false);

        // Test 7: Diagonal ascendente
        String[] mutant5 = {
            "ATGCGA",
            "CAGTAC",
            "TTATAT",
            "AGAAAG",
            "CACCTA",
            "TCACTG"
        };
        testDna("Test 7 - Con diagonales", mutant5, detector, true);

        System.out.println("\n========================================");
        System.out.println("TODAS LAS PRUEBAS COMPLETADAS");
        System.out.println("========================================");
    }

    private static void testDna(String name, String[] dna, MutantDetector detector, boolean expected) {
        System.out.println("\n" + name);
        System.out.println("Matriz:");
        for (String row : dna) {
            System.out.println("  " + row);
        }

        boolean result = detector.isMutant(dna);
        String resultText = result ? "✅ ES MUTANTE" : "❌ NO ES MUTANTE";
        String expectedText = expected ? "ES MUTANTE" : "NO ES MUTANTE";

        System.out.println("Resultado: " + resultText);
        System.out.println("Esperado:  " + expectedText);

        if (result == expected) {
            System.out.println("✅ PASS");
        } else {
            System.out.println("❌ FAIL");
        }
    }
}


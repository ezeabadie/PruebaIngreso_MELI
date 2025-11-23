package org.example.exception;

/**
 * Excepción lanzada cuando falla el cálculo del hash SHA-256 del ADN.
 */
public class DnaHashCalculationException extends RuntimeException {

    public DnaHashCalculationException(String message) {
        super(message);
    }

    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}


package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador personalizado para secuencias de ADN.
 * Implementa la lógica de validación de @ValidDnaSequence.
 */
public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final int MIN_SIZE = 4;
    private static final Pattern DNA_PATTERN = Pattern.compile("^[ATCG]+$");

    @Override
    public void initialize(ValidDnaSequence constraintAnnotation) {
        // No se necesita inicialización especial
    }

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // Null check
        if (dna == null || dna.length == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La secuencia de ADN no puede estar vacía")
                   .addConstraintViolation();
            return false;
        }

        final int n = dna.length;

        // Validar tamaño mínimo
        if (n < MIN_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "La secuencia de ADN debe tener al menos " + MIN_SIZE + "x" + MIN_SIZE)
                   .addConstraintViolation();
            return false;
        }

        // Validar que sea NxN y solo contenga A, T, C, G
        for (int i = 0; i < n; i++) {
            String row = dna[i];

            // Validar que no sea null
            if (row == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "La fila " + i + " no puede ser nula")
                       .addConstraintViolation();
                return false;
            }

            // Validar que tenga el largo correcto (NxN)
            if (row.length() != n) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "La matriz debe ser NxN. Fila " + i + " tiene longitud " + row.length() +
                    " pero se esperaba " + n)
                       .addConstraintViolation();
                return false;
            }

            // Validar que solo contenga A, T, C, G
            if (!DNA_PATTERN.matcher(row).matches()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "La fila " + i + " contiene caracteres inválidos. Solo se permiten: A, T, C, G")
                       .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}


package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación personalizada para validar secuencias de ADN.
 *
 * Reglas de validación:
 * - La matriz debe ser NxN (cuadrada)
 * - Tamaño mínimo: 4x4
 * - Solo puede contener los caracteres: A, T, C, G
 * - No puede haber filas nulas o vacías
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
@Documented
public @interface ValidDnaSequence {

    String message() default "Secuencia de ADN inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


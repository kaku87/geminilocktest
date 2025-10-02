package com.example.optimisticlock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EntityConfiguredValidator.class)
@Inherited
public @interface EntityConfigured {

    String message() default "Entity configuration is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package br.com.sergioluigi.personalfinancialcontrol.infra.controller.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BalanceMustBeGreaterOrEqualToLimitValidator.class)
public @interface BalanceMustBeGreaterOrEqualToLimit {

    String message() default "Account balance must be greater or equal to limit";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

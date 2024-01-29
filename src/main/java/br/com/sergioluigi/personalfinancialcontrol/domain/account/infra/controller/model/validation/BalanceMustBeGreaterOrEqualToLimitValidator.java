package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model.validation;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model.AccountRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BalanceMustBeGreaterOrEqualToLimitValidator
        implements ConstraintValidator<BalanceMustBeGreaterOrEqualToLimit, AccountRequest> {

    @Override
    public void initialize(BalanceMustBeGreaterOrEqualToLimit constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AccountRequest value, ConstraintValidatorContext context) {
        return value.overdraftLimit() + value.balance() >= 0;
    }
}

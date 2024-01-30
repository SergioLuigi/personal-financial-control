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

        if(value.getOverdraftLimit() != null && value.getBalance() != null) {
            return value.getOverdraftLimit() + value.getBalance() >= 0;
        }

        return true;
    }
}

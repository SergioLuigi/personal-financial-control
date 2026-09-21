package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;

import java.math.BigDecimal;

public record BankAccountResponse(
        String id,
        String name,
        String description,
        BigDecimal balance
) {

    public static BankAccountResponse from(BankAccount bankAccount) {
        return new BankAccountResponse(
                bankAccount.id(),
                bankAccount.name(),
                bankAccount.description(),
                bankAccount.balance()
        );
    }
}

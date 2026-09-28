package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record BankAccountResponse(
        UUID id,
        String name,
        @Nullable String description,
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

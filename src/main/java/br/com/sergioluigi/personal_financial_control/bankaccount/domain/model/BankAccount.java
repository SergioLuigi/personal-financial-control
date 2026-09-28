package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record BankAccount(
        UUID id,
        String owner,
        String name,
        @Nullable String description,
        BigDecimal balance
) {

    private static final int BALANCE_SCALE = 2;

    public static BankAccount create(String owner, NewBankAccount newBankAccount) {
        var balance = Objects.requireNonNullElse(newBankAccount.balance(), BigDecimal.ZERO);

        return new BankAccount(
                UUID.randomUUID(),
                owner,
                newBankAccount.name(),
                newBankAccount.description(),
                balance.setScale(BALANCE_SCALE)
        );
    }
}

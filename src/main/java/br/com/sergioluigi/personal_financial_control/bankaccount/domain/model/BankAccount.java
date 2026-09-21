package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record BankAccount(
        String id,
        String name,
        String description,
        BigDecimal balance,
        String createdBy,
        String updatedBy,
        Instant createdAt,
        Instant updatedAt
) {

    public BankAccount merge(BankAccount bankAccount) {
        return new BankAccount(
                this.id,
                Objects.nonNull(bankAccount.name) && !Objects.equals(this.name, bankAccount.name) ? bankAccount.name : this.name,
                Objects.nonNull(bankAccount.description) && !Objects.equals(this.description, bankAccount.description) ? bankAccount.description : this.description,
                Objects.nonNull(bankAccount.balance) && !Objects.equals(this.balance, bankAccount.balance) ? bankAccount.balance : this.balance,
                this.createdBy,
                this.updatedBy,
                this.createdAt,
                this.updatedAt
        );
    }
}

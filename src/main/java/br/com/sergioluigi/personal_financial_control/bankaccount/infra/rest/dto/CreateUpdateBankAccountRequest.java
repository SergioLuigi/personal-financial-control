package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateUpdateBankAccountRequest(

        @NotBlank
        @Size(min = 3, max = 150)
        String name,

        @NotBlank
        @Size(min = 3, max = 150)
        String description,

        @NotNull
        @PositiveOrZero
        @Digits(integer = 17, fraction = 2)
        BigDecimal balance
) {

    public BankAccount toDomain() {
        return new BankAccount(null, name, description, balance, null, null, null, null);
    }
}

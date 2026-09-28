package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

public record CreateBankAccountRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must be at most 60 characters")
        @Nullable String name,

        @Size(max = 255, message = "Description must be at most 255 characters")
        @Nullable String description,

        @Digits(integer = 11, fraction = 2, message = "Enter a valid amount")
        @Nullable BigDecimal balance
) {

    // Normalized before validation, so that lengths are checked on the trimmed values.
    public CreateBankAccountRequest {
        name = name == null ? null : name.strip();
        description = description == null || description.isBlank() ? null : description.strip();
    }

    public NewBankAccount toDomain() {
        return new NewBankAccount(Objects.requireNonNull(name), description, balance);
    }
}

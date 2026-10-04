package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * The body of the request that creates a bank account.
 *
 * @param name the name, required and at most 60 characters
 * @param description a free text, at most 255 characters
 * @param balance the initial balance, with at most two decimal places; zero when absent
 */
public record CreateBankAccountRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must be at most 60 characters")
        @Nullable String name,

        @Size(max = 255, message = "Description must be at most 255 characters")
        @Nullable String description,

        @Digits(integer = 11, fraction = 2, message = "Enter a valid amount")
        @Nullable BigDecimal balance
) {

    /** Trims the name before it is validated. */
    public CreateBankAccountRequest {
        name = name == null ? null : name.strip();
    }

    /**
     * Converts the request into the domain model.
     *
     * @return the account to create
     */
    public NewBankAccount toDomain() {
        return new NewBankAccount(name, description, balance);
    }
}

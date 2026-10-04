package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * A bank account about to be created. The name is stored trimmed and in lowercase,
 * the description is empty when none is given and the balance defaults to zero.
 *
 * @param name the name of the account
 * @param description a free text about the account
 * @param balance the initial balance, with two decimal places
 */
public record NewBankAccount(String name, String description, BigDecimal balance) {

    /** Normalizes the name, the description and the balance. */
    public NewBankAccount {
        name = name.strip().toLowerCase(Locale.ROOT);
        description = description == null ? "" : description;
        balance = balance == null ? BigDecimal.ZERO.setScale(2) : balance.setScale(2);
    }
}

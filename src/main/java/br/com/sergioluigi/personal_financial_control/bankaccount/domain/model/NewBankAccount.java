package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;

public record NewBankAccount(
        String name,
        @Nullable String description,
        @Nullable BigDecimal balance
) {

    public NewBankAccount {
        name = name.strip().toLowerCase(Locale.ROOT);
    }
}

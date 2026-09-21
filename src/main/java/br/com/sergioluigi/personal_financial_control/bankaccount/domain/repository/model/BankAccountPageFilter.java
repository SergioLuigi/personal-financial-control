package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model;

import org.jspecify.annotations.Nullable;

public record BankAccountPageFilter(
        @Nullable String name,
        @Nullable String description,
        @Nullable BankAccountBalanceRangeFilter balanceRange
) {

    public static BankAccountPageFilter unfiltered() {
        return new BankAccountPageFilter(null, null, null);
    }
}

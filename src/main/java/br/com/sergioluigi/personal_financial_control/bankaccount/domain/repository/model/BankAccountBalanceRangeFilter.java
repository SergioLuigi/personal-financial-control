package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public record BankAccountBalanceRangeFilter(

        @Nullable BigDecimal from,

        @Nullable BigDecimal to
) {

    public boolean isEmpty() {
        return from == null && to == null;
    }
}

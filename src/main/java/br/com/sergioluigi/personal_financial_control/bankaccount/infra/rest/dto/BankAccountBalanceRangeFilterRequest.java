package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import jakarta.validation.constraints.Digits;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public record BankAccountBalanceRangeFilterRequest(

        @Digits(integer = 17, fraction = 2)
        @Nullable BigDecimal from,

        @Digits(integer = 17, fraction = 2)
        @Nullable BigDecimal to
) {
}

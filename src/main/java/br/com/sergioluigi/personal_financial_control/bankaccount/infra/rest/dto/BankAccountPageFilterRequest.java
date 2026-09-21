package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountBalanceRangeFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountPageFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record BankAccountPageFilterRequest(
        @Size(min = 3, max = 150)
        @Nullable String name,

        @Size(min = 3, max = 150)
        @Nullable String description,

        @Valid
        @Nullable BankAccountBalanceRangeFilterRequest balanceRange
) {

        @AssertTrue(message = "Balance range 'to' must be greater than or equal to 'from'")
        public boolean isRangeValid() {

                if (balanceRange == null || balanceRange.from() == null || balanceRange.to() == null) {
                        return true;
                }

                return balanceRange.to().compareTo(balanceRange.from()) >= 0;
        }

        public BankAccountPageFilter toDomain() {
                return new BankAccountPageFilter(
                        name,
                        description,
                        balanceRange == null
                                ? null
                                : new BankAccountBalanceRangeFilter(balanceRange.from(), balanceRange.to())
                );
        }
}

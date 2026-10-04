package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.BindParam;

import java.math.BigDecimal;

/**
 * The query parameters that filter the list of bank accounts; every one is optional.
 *
 * @param name text the name must contain
 * @param description text the description must contain
 * @param minBalance the lowest balance accepted
 * @param maxBalance the highest balance accepted
 */
public record ListBankAccountsRequest(
        @Nullable String name,
        @Nullable String description,
        @BindParam("min_balance") @Nullable BigDecimal minBalance,
        @BindParam("max_balance") @Nullable BigDecimal maxBalance
) {

    /**
     * Turns the parameters into the filter of the list.
     *
     * @return the filter
     */
    public BankAccountFilter toDomain() {
        return new BankAccountFilter(name, description, minBalance, maxBalance);
    }
}

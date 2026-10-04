package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.BankAccountBalanceSource;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** The one place where a bank account's balance is calculated (Glossary, "Balance"). */
@Component
@RequiredArgsConstructor
class BankAccountBalances {

    /** Every module that adds to, or takes off, an account's balance. */
    private final List<BankAccountBalanceSource> sources;

    /**
     * The account as the user sees it: its balance is what the account was stored with plus every source.
     *
     * @param stored the account as stored
     * @return the account with its effective balance
     */
    BankAccount effective(BankAccount stored) {
        return new BankAccount(
                stored.id(), stored.owner(), stored.name(), stored.description(),
                stored.balance().add(contributions(stored)).setScale(2));
    }

    /**
     * The balance to store so that, with every source, the account shows {@code desired} (UC-04 FR-09).
     *
     * @param stored the account as stored
     * @param desired the balance the account must show
     * @return the balance to store
     */
    BigDecimal storedFor(BankAccount stored, BigDecimal desired) {
        return desired.subtract(contributions(stored)).setScale(2);
    }

    /**
     * The sum of what every source contributes to the account.
     *
     * @param account the account to calculate for
     * @return the signed total
     */
    private BigDecimal contributions(BankAccount account) {
        return sources.stream()
                .map(source -> source.amount(account.owner(), account.id()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

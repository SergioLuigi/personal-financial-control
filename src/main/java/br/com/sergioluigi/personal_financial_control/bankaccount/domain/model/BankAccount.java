package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A bank account of a user.
 *
 * @param id the id of the account
 * @param owner the user who owns the account
 * @param name the name of the account, unique for its owner, in lowercase
 * @param description a free text about the account; empty when none was given
 * @param balance the balance of the account
 */
public record BankAccount(UUID id, String owner, String name, String description, BigDecimal balance) {

    /**
     * Applies the fields supplied in {@code changes}; the others keep their value.
     *
     * @param changes the fields to change
     * @return the account with the changes applied
     */
    public BankAccount apply(BankAccountChanges changes) {
        return new BankAccount(
                id,
                owner,
                changes.name() == null ? name : changes.name(),
                changes.description() == null ? description : changes.description(),
                changes.balance() == null ? balance : changes.balance());
    }

    /**
     * The same account with another balance.
     *
     * @param newBalance the balance of the new account
     * @return the account with that balance
     */
    public BankAccount withBalance(BigDecimal newBalance) {
        return new BankAccount(id, owner, name, description, newBalance);
    }
}

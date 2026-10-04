package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A bank account as the API returns it.
 *
 * @param id the id of the account
 * @param name the name of the account, in lowercase
 * @param description the free text about the account
 * @param balance the balance the account shows
 */
public record BankAccountResponse(UUID id, String name, String description, BigDecimal balance) {

    /**
     * Builds the response from the domain model.
     *
     * @param bankAccount the account
     * @return the response
     */
    public static BankAccountResponse from(BankAccount bankAccount) {
        return new BankAccountResponse(
                bankAccount.id(), bankAccount.name(), bankAccount.description(), bankAccount.balance());
    }
}

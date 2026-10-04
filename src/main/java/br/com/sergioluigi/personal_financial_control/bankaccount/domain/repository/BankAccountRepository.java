package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Stores and reads bank accounts. Every query is scoped to an owner. */
public interface BankAccountRepository {

    /**
     * Stores a new account.
     *
     * @param owner the user who owns the account
     * @param bankAccount the account to store
     * @return the account stored
     */
    BankAccount save(String owner, NewBankAccount bankAccount);

    /**
     * Stores the new values of an existing account.
     *
     * @param bankAccount the account with its new values
     * @return the account stored
     */
    BankAccount update(BankAccount bankAccount);

    /**
     * Reads an account of the owner.
     *
     * @param owner the user who must own the account
     * @param id the id of the account
     * @return the account, or empty when the owner has none with that id
     */
    Optional<BankAccount> findByOwnerAndId(String owner, UUID id);

    /**
     * The owner's accounts matching the filter, except for its balance bounds, ordered by name and id.
     *
     * @param owner the user who owns the accounts
     * @param filter the criteria of the list
     * @return the accounts found
     */
    List<BankAccount> findAllByOwner(String owner, BankAccountFilter filter);

    /**
     * Reads the owner's accounts among those ids.
     *
     * @param owner the user who owns the accounts
     * @param ids the ids to look for
     * @return the accounts found
     */
    List<BankAccount> findAllByOwnerAndIdIn(String owner, Collection<UUID> ids);

    /**
     * Tells whether the owner has an account with that name.
     *
     * @param owner the user who owns the accounts
     * @param name the name to look for
     * @return {@code true} when there is one
     */
    boolean existsByOwnerAndName(String owner, String name);

    /**
     * Tells whether the owner has an account with that name other than the one given.
     *
     * @param owner the user who owns the accounts
     * @param name the name to look for
     * @param excludedId the id of the account to leave out
     * @return {@code true} when there is one
     */
    boolean existsByOwnerAndNameExcludingId(String owner, String name, UUID excludedId);

    /**
     * Deletes the bank account: physically, or logically when {@code keepRecord} so the closings can still refer to it.
     *
     * @param account the bank account to delete
     * @param keepRecord whether to keep the record, hidden
     */
    void delete(BankAccount account, boolean keepRecord);
}

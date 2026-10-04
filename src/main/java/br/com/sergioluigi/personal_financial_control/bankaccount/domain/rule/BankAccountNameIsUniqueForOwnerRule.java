package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** An owner cannot have two bank accounts with the same name. */
@Component
@RequiredArgsConstructor
public class BankAccountNameIsUniqueForOwnerRule {

    /** Looks for accounts with the name. */
    private final BankAccountRepository repository;

    /**
     * Checks the name for a new account.
     *
     * @param owner the user who owns the accounts
     * @param name the name to check
     * @throws BusinessException when the owner already has an account with that name
     */
    public void check(String owner, String name) {
        if (repository.existsByOwnerAndName(owner, name)) {
            throw new BusinessException(BankAccountMessage.NAME_ALREADY_EXISTS);
        }
    }

    /**
     * Same check for an existing account, which may keep its own name.
     *
     * @param owner the user who owns the accounts
     * @param name the name to check
     * @param accountId the id of the account that is being changed
     * @throws BusinessException when another account of the owner has that name
     */
    public void check(String owner, String name, UUID accountId) {
        if (repository.existsByOwnerAndNameExcludingId(owner, name, accountId)) {
            throw new BusinessException(BankAccountMessage.NAME_ALREADY_EXISTS);
        }
    }
}

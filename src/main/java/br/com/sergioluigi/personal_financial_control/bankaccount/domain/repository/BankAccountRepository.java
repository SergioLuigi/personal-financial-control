package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;

public interface BankAccountRepository {

    BankAccount create(BankAccount bankAccount);

    /**
     * Names are compared ignoring case and accents.
     */
    boolean existsByOwnerAndName(String owner, String name);
}

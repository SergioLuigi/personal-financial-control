package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;

/** Creates a bank account for the current user. */
public interface CreateBankAccountUseCase {

    /**
     * Creates the account.
     *
     * @param owner the authenticated user
     * @param bankAccount the account to create
     * @return the account created
     */
    BankAccount execute(String owner, NewBankAccount bankAccount);
}

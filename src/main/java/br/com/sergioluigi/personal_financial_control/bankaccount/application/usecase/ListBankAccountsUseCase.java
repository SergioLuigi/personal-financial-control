package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Lists the bank accounts of the current user. */
public interface ListBankAccountsUseCase {

    /**
     * Lists one page of the accounts that match the filter.
     *
     * @param owner the authenticated user
     * @param filter the criteria of the list
     * @param pageable the page, its size and its order
     * @return the page of accounts, ordered by name
     */
    Page<BankAccount> execute(String owner, BankAccountFilter filter, Pageable pageable);
}

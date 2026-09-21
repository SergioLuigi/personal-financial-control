package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountPageFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindBankAccountsPageUseCase {

    Page<BankAccount> execute(BankAccountPageFilter filter, Pageable pageable);
}

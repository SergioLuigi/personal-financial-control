package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.FindBankAccountsPageUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountPageFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindBankAccountsPageUseCaseImpl implements FindBankAccountsPageUseCase {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public Page<BankAccount> execute(BankAccountPageFilter filter, Pageable pageable) {
        return bankAccountRepository.findPage(filter, pageable);
    }
}

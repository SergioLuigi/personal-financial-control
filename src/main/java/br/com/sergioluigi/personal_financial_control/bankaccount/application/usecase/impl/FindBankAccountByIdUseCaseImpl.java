package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.FindBankAccountByIdUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.commons.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class FindBankAccountByIdUseCaseImpl implements FindBankAccountByIdUseCase {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount execute(String id) {
        return bankAccountRepository
                .findByIdAndOwner(id)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));
    }
}

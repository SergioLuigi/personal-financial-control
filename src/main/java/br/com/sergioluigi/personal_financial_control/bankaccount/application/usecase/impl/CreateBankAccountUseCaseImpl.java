package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateBankAccountUseCaseImpl implements CreateBankAccountUseCase {

    private final BankAccountRepository bankAccountRepository;

    private final BankAccountNameIsUniqueForOwnerRule nameIsUniqueForOwner;

    @Override
    @Transactional
    public BankAccount execute(BankAccount bankAccount) {

        nameIsUniqueForOwner.check(bankAccount.name());

        return bankAccountRepository.save(bankAccount);
    }
}

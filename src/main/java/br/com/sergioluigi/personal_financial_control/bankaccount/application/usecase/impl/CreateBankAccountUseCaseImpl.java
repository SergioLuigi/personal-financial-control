package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateBankAccountUseCaseImpl implements CreateBankAccountUseCase {

    private final CurrentUser currentUser;

    private final BankAccountNameIsUniqueForOwnerRule nameIsUniqueForOwner;

    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public BankAccount execute(NewBankAccount newBankAccount) {
        var owner = currentUser.getUsername();

        nameIsUniqueForOwner.check(owner, newBankAccount.name());

        return bankAccountRepository.create(BankAccount.create(owner, newBankAccount));
    }
}

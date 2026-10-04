package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implements {@link CreateBankAccountUseCase}. */
@Service
@RequiredArgsConstructor
class CreateBankAccountUseCaseImpl implements CreateBankAccountUseCase {

    /** Rejects a name the owner already uses. */
    private final BankAccountNameIsUniqueForOwnerRule nameIsUniqueForOwnerRule;
    /** Stores the account. */
    private final BankAccountRepository repository;

    /** Checks the name is free for the current user, then stores the account. */
    @Override
    @Transactional
    public BankAccount execute(String owner, NewBankAccount bankAccount) {
        nameIsUniqueForOwnerRule.check(owner, bankAccount.name());

        return repository.save(owner, bankAccount);
    }
}

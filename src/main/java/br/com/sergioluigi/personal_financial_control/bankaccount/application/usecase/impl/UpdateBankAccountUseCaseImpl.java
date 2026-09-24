package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.UpdateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import br.com.sergioluigi.personal_financial_control.commons.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
class UpdateBankAccountUseCaseImpl implements UpdateBankAccountUseCase {

    private final BankAccountRepository bankAccountRepository;

    private final BankAccountNameIsUniqueForOwnerRule nameIsUniqueForOwner;

    @Override
    @Transactional
    public BankAccount execute(String id, BankAccount changes) {

        var existing = bankAccountRepository
                .findByIdAndOwner(id)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));

        if (!existing.name().equals(changes.name())) {
            nameIsUniqueForOwner.check(changes.name());
        }

        return bankAccountRepository.save(existing.merge(changes));
    }
}

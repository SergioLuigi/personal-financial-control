package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.commons.exception.AlreadyExistsException;
import org.springframework.stereotype.Component;

@Component
public class BankAccountNameIsUniqueForOwnerRule {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountNameIsUniqueForOwnerRule(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public void check(String name) {
        if (name != null) {
            bankAccountRepository
                    .findByNameAndOwner(name)
                    .ifPresent(existing -> {
                        throw new AlreadyExistsException("Bank account with name '" + name + "' already exists");
                    });
        }
    }
}

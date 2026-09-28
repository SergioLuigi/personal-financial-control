package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;

@Component
@RequiredArgsConstructor
public class BankAccountNameIsUniqueForOwnerRule {

    private final BankAccountRepository bankAccountRepository;

    public void check(String owner, String name) {
        if (bankAccountRepository.existsByOwnerAndName(owner, name)) {
            throw new BusinessException(NAME_ALREADY_EXISTS);
        }
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.UpdateAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateAccountRepositoryImpl implements UpdateAccountRepository {

    private final AccountRepository accountRepository;

    @Override
    public AccountModel execute(AccountModel accountModel) {
        return accountRepository.save(AccountEntity.of(accountModel)).toModel();
    }
}

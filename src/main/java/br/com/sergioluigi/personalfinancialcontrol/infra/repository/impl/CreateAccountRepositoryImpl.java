package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.CreateAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateAccountRepositoryImpl implements CreateAccountRepository {

    private final AccountRepository accountRepository;

    @Override
    public AccountModel execute(AccountModel accountModel){
        return accountRepository.save(AccountEntity.of(accountModel)).toModel();
    }
}

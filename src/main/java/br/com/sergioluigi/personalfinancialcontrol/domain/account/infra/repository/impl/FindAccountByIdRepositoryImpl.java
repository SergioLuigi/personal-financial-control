package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.FindAccountByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FindAccountByIdRepositoryImpl implements FindAccountByIdRepository {

    private final AccountRepository accountRepository;

    @Override
    public Optional<AccountModel> execute(UUID id) {
        return accountRepository.findById(id).map(AccountEntity::toModel);
    }
}

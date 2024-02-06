package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindAccountByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.AccountEntity;
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

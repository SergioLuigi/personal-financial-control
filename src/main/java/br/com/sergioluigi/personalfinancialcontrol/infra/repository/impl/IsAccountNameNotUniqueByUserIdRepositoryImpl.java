package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsAccountNameNotUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class IsAccountNameNotUniqueByUserIdRepositoryImpl implements IsAccountNameNotUniqueByUserIdRepository {

    private final AccountRepository accountRepository;

    @Override
    public Boolean execute(String name, UUID id) {
        return accountRepository.existsByNameAndUser_id(name, id);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsAccountNameUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class IsAccountNameUniqueByUserIdRepositoryImpl implements IsAccountNameUniqueByUserIdRepository {

    private final AccountRepository accountRepository;

    @Override
    public Boolean execute(String name, UUID id) {
        return accountRepository.existsByNameAndUser_id(name, id);
    }
}

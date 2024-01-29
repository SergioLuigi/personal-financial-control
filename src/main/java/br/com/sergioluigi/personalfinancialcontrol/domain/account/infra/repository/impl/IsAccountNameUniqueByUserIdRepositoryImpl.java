package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.IsAccountNameUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class IsAccountNameUniqueByUserIdRepositoryImpl implements IsAccountNameUniqueByUserIdRepository {

    private final AccountRepository accountRepository;

    @Override
    public Boolean execute(String name, UUID id) {
        return !accountRepository.existsByNameAndUser_id(name, id);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.GetAddedAccountBalancesRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetAddedAccountBalancesRepositoryImpl implements GetAddedAccountBalancesRepository {

    private final AccountRepository accountRepository;

    public Double execute(String username) {
        return accountRepository.findAllByUser_username(username)
                        .stream()
                        .mapToDouble(AccountEntity::getBalance)
                        .sum();
    }
}
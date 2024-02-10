package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.AdjustAccountBalanceRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class AdjustAccountBalanceRepositoryImpl implements AdjustAccountBalanceRepository {

    private final AccountRepository accountRepository;

    @Override
    public AccountModel execute(UUID id, Double amount) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND));
        account.setBalance(amount);
        return accountRepository.save(account).toModel();
    }
}

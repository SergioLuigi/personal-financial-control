package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.AdjustAccountLimitRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class AdjustAccountLimitRepositoryImpl implements AdjustAccountLimitRepository {

    private final AccountRepository accountRepository;

    @Override
    public AccountModel execute(UUID id, Double amount) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND));
        account.setOverdraftLimit(amount);
        return accountRepository.save(account).toModel();
    }
}

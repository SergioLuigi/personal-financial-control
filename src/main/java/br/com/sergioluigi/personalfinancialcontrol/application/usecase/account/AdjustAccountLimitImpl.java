package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.AdjustAccountLimitRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.AdjustAccountBalance;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.AdjustAccountLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdjustAccountLimitImpl implements AdjustAccountLimit {

    private final AdjustAccountLimitRepository adjustAccountLimitRepository;

    @Override
    public AccountModel execute(UUID id, Double amount) {
        return adjustAccountLimitRepository.execute(id, amount);
    }
}

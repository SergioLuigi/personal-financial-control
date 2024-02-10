package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.AdjustAccountBalanceRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.AdjustAccountBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdjustAccountBalanceImpl implements AdjustAccountBalance {

    private final AdjustAccountBalanceRepository adjustAccountBalanceRepository;

    @Override
    public AccountModel execute(UUID id, Double amount) {
        return adjustAccountBalanceRepository.execute(id, amount);
    }
}

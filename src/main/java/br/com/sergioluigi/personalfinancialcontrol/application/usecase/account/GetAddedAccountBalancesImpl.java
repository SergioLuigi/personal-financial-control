package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.GetAddedAccountBalancesRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.GetAddedAccountBalances;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAddedAccountBalancesImpl implements GetAddedAccountBalances {

    private final GetAddedAccountBalancesRepository getAddedAccountBalancesRepository;

    public Double execute(String username){
        return getAddedAccountBalancesRepository.execute(username);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

import java.util.UUID;

public interface AdjustAccountLimitRepository {
    AccountModel execute(UUID id, Double amount);
}

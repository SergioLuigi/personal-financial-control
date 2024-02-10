package br.com.sergioluigi.personalfinancialcontrol.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

import java.util.UUID;

public interface AdjustAccountBalance {
    AccountModel execute(UUID id, Double amount);
}

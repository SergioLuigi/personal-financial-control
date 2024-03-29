package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        Double overdraftLimit,
        Double balance,
        AccountType type,
        LocalDateTime createdOn,
        LocalDateTime lastUpdateOn
) {
    public AccountResponse(AccountModel accountModel) {
        this(accountModel.getId(), accountModel.getName(),
                accountModel.getOverdraftLimit(), accountModel.getBalance(),
                accountModel.getType(), accountModel.getCreatedOn(),accountModel.getLastUpdateOn());
    }
}

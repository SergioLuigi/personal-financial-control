package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        Double overdraftLimit,
        Double balance,
        LocalDateTime createdOn,
        LocalDateTime lastUpdateOn
) {
    public AccountResponse(AccountModel accountModel) {
        this(accountModel.getId(), accountModel.getName(),
                accountModel.getOverdraftLimit(), accountModel.getBalance(),
                accountModel.getCreatedOn(),accountModel.getLastUpdateOn());
    }
}

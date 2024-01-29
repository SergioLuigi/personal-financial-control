package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model.validation.BalanceMustBeGreaterOrEqualToLimit;
import jakarta.validation.constraints.NotNull;

@BalanceMustBeGreaterOrEqualToLimit
public record AccountRequest(
        @NotNull
        String name,
        @NotNull
        Double overdraftLimit,
        @NotNull
        Double balance
) {

    public AccountModel toModel() {
        return AccountModel.builder()
                .name(name)
                .overdraftLimit(overdraftLimit)
                .balance(balance)
                .build();
    }
}

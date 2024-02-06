package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.validation.annotation.BalanceMustBeGreaterOrEqualToLimit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@BalanceMustBeGreaterOrEqualToLimit
public class AccountRequest{

    @NotNull
    @Size(min = 3, max = 150)
    private String name;

    @NotNull
    private Double overdraftLimit;

    @NotNull
    private Double balance;

    public AccountModel toModel() {
        return AccountModel.builder()
                .name(name)
                .overdraftLimit(overdraftLimit)
                .balance(balance)
                .build();
    }

    public AccountModel toModelBasedOn(AccountModel oldModel) {
        return AccountModel.builder()
                .id(oldModel.getId())
                .name(name)
                .overdraftLimit(overdraftLimit)
                .balance(balance)
                .user(oldModel.getUser())
                .createdOn(oldModel.getCreatedOn())
                .lastUpdateOn(oldModel.getLastUpdateOn())
                .build();
    }
}

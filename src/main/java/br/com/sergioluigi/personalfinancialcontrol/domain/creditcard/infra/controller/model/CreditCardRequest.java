package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreditCardRequest(

        @NotNull
        UUID accountId,

        @NotNull
        @Size(min = 3, max = 150)
        String name,

        @NotNull
        Double limit,

        @NotNull
        Double balance,

        @NotNull
        LocalDate dueDate,

        @NotNull
        LocalDate closingDate
) {
    public CreditCardModel toModel() {
        return CreditCardModel.builder()
                .name(name)
                .limit(limit)
                .balance(balance)
                .dueDate(dueDate)
                .accountModel(AccountModel.builder()
                        .id(accountId).build())
                .closingDate(closingDate)
                .build();
    }
}

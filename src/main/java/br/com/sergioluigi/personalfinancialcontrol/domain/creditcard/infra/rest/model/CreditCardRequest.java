package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.rest.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;

import java.time.LocalDate;

public record CreditCardRequest(
        //Account account,

        String name,

        Double limit,

        Double balance,

        LocalDate dueDate,

        LocalDate closingDate
) {
    public CreditCardModel toModel() {
        return CreditCardModel.builder()
                .name(name)
                .limit(limit)
                .balance(balance)
                .dueDate(dueDate)
                .closingDate(closingDate)
                .build();
    }
}

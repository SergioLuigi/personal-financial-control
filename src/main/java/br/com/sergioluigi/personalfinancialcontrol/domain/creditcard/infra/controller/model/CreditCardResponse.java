package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;

import java.time.LocalDate;
import java.util.UUID;

public record CreditCardResponse(
        UUID id,
        String name,
        Double limit,
        Double balance,
        LocalDate dueDate,
        LocalDate closingDate
) {

    public CreditCardResponse(CreditCardModel creditCardModel) {
        this(creditCardModel.getId(),creditCardModel.getName(),
                creditCardModel.getLimit(),creditCardModel.getBalance(),
                creditCardModel.getDueDate(),creditCardModel.getClosingDate());
    }
}

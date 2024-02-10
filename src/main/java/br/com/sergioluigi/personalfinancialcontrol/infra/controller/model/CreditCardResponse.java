package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

public record CreditCardResponse(
        UUID id,
        UUID accountId,
        String name,
        Double limit,
        Double balance,
        LocalDate dueDate,
        LocalDate closingDate
) {

    public CreditCardResponse(CreditCardModel creditCardModel) {
        this(creditCardModel.getId(),
                Optional.ofNullable(creditCardModel.getAccountModel())
                        .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND)).getId(),
                creditCardModel.getName(),
                creditCardModel.getLimit(),creditCardModel.getBalance(),
                creditCardModel.getDueDate(),creditCardModel.getClosingDate());
    }
}

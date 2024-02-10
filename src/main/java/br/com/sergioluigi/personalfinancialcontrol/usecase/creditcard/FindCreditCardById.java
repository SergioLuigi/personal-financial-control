package br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

import java.util.UUID;

public interface FindCreditCardById {
    CreditCardModel execute(UUID id);
}

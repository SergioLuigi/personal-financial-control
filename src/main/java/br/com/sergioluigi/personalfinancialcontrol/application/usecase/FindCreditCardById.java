package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

import java.util.UUID;

public interface FindCreditCardById {
    CreditCardModel execute(UUID id);
}

package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

public interface SaveCreditCard {
    CreditCardModel execute(String username, CreditCardModel creditCardModel);
}

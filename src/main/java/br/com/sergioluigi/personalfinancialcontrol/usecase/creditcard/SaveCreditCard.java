package br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

public interface SaveCreditCard {
    CreditCardModel execute(String username, CreditCardModel creditCardModel);
}

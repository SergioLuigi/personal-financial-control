package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

public interface UpdateCreditCardRepository {
    CreditCardModel execute(CreditCardModel creditCardModel);
}

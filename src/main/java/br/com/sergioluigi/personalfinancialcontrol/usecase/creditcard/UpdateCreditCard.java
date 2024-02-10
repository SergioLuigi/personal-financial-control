package br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

import java.util.Map;
import java.util.UUID;

public interface UpdateCreditCard {
    CreditCardModel execute(UUID id, CreditCardModel creditCardModel);

    CreditCardModel execute(UUID id, Map<String, Object> properties);
}

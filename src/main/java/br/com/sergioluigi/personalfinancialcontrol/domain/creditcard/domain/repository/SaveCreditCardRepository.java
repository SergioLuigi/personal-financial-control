package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;


import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;

public interface SaveCreditCardRepository {
    CreditCardModel execute(CreditCardModel creditCardModel);
}

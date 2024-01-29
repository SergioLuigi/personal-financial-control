package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;


import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;

import java.util.Optional;
import java.util.UUID;

public interface FindCreditCardByIdRepository {
    Optional<CreditCardModel> execute(UUID id);
}

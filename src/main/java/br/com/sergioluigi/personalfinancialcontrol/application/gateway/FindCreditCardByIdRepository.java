package br.com.sergioluigi.personalfinancialcontrol.application.gateway;


import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;

import java.util.Optional;
import java.util.UUID;

public interface FindCreditCardByIdRepository {
    Optional<CreditCardModel> execute(UUID id);
}

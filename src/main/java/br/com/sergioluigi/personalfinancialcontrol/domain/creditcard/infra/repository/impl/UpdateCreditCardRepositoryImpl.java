package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.UpdateCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity.CreditCardEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateCreditCardRepositoryImpl implements UpdateCreditCardRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public CreditCardModel execute(CreditCardModel creditCardModel) {
        return creditCardRepository.save(new CreditCardEntity(creditCardModel)).toModel();
    }
}

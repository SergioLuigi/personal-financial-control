package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.SaveCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CreditCardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SaveCreditCardRepositoryImpl implements SaveCreditCardRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public CreditCardModel execute(CreditCardModel creditCardModel) {
        return creditCardRepository.save(CreditCardEntity.of(creditCardModel)).toModel();
    }
}

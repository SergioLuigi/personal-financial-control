package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CreditCardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateCreditCardRepositoryImpl implements UpdateCreditCardRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public CreditCardModel execute(CreditCardModel creditCardModel) {
        return creditCardRepository.save(CreditCardEntity.of(creditCardModel)).toModel();
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity.CreditCardEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.FindCreditCardByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FindCreditCardByIdRepositoryImpl implements FindCreditCardByIdRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public Optional<CreditCardModel> execute(UUID id) {
        return creditCardRepository.findById(id).map(CreditCardEntity::toModel);
    }
}

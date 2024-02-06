package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CreditCardEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCreditCardByIdRepository;
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

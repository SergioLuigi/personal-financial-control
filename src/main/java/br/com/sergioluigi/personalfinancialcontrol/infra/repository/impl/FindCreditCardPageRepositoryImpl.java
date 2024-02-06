package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCreditCardPageRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.CreditCardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindCreditCardPageRepositoryImpl implements FindCreditCardPageRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public Page<CreditCardModel> execute(PageRequest pageRequest) {
        return creditCardRepository.findAll(pageRequest).map(CreditCardEntity::toModel);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.FindCreditCardPageRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity.CreditCardEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
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

package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.IsCreditCardUniqueByNameRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IsCreditCardUniqueByNameRepositoryImpl implements IsCreditCardUniqueByNameRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public Boolean check(String name) {
        return !creditCardRepository.existsByName(name);
    }
}

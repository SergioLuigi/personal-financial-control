package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.IsCreditCardNameUniqueByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IsCreditCardNameUniqueByUsernameRepositoryImpl implements IsCreditCardNameUniqueByUsernameRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public Boolean check(String name, String username) {
        return !creditCardRepository.existsByNameAndAccount_User_username(name, username);
    }
}

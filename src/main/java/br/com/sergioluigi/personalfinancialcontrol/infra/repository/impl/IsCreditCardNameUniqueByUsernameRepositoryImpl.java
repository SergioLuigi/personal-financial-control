package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCreditCardNameUniqueByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.CreditCardRepository;
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

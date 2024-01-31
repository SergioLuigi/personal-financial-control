package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.IsCreditCardNameUniqueByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NAME_ALREADY_IN_USE;

@Service
@RequiredArgsConstructor
public class IsCreditCardUniqueByNameService {

    private final IsCreditCardNameUniqueByUsernameRepository isCreditCardNameUniqueByUsernameRepository;

    public void check(String name, String username) {
        if (!isCreditCardNameUniqueByUsernameRepository.check(name, username)) {
            throw new ApplicationException(CREDIT_CARD_NAME_ALREADY_IN_USE);
        }
    }
}

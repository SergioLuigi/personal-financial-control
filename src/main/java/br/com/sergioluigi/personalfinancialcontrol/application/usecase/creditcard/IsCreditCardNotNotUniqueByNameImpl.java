package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCreditCardNameNotUniqueByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.IsCreditCardNotUniqueByName;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NAME_ALREADY_IN_USE;

@Service
@RequiredArgsConstructor
public class IsCreditCardNotNotUniqueByNameImpl implements IsCreditCardNotUniqueByName {

    private final IsCreditCardNameNotUniqueByUsernameRepository isCreditCardNameNotUniqueByUsernameRepository;

    @Override
    public void check(String name, String username) {
        if (isCreditCardNameNotUniqueByUsernameRepository.check(name, username)) {
            throw new ApplicationException(CREDIT_CARD_NAME_ALREADY_IN_USE);
        }
    }
}

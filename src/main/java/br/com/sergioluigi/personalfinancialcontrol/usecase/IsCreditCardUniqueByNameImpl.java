package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsCreditCardNameUniqueByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.IsCreditCardUniqueByName;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NAME_ALREADY_IN_USE;

@Service
@RequiredArgsConstructor
public class IsCreditCardUniqueByNameImpl implements IsCreditCardUniqueByName {

    private final IsCreditCardNameUniqueByUsernameRepository isCreditCardNameUniqueByUsernameRepository;

    @Override
    public void check(String name, String username) {
        if (!isCreditCardNameUniqueByUsernameRepository.check(name, username)) {
            throw new ApplicationException(CREDIT_CARD_NAME_ALREADY_IN_USE);
        }
    }
}

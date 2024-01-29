package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.IsCreditCardUniqueByNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NAME_ALREADY_IN_USE;

@Service
@RequiredArgsConstructor
public class IsCreditCardUniqueByNameService {

    private final IsCreditCardUniqueByNameRepository isCreditCardUniqueByNameRepository;

    public void check(String name) {
        if (!isCreditCardUniqueByNameRepository.check(name)) {
            throw new ApplicationException(CREDIT_CARD_NAME_ALREADY_IN_USE);
        }
    }

    public void check(String entityName, String name) {
        if (!entityName.equals(name)) {
            check(name);
        }
    }
}

package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.FindCreditCardById;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCreditCardByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindCreditCardByIdImpl implements FindCreditCardById {

    private final FindCreditCardByIdRepository findCreditCardByIdRepository;

    @Override
    @Transactional
    public CreditCardModel execute(UUID id) {
        return findCreditCardByIdRepository
                .execute(id).orElseThrow(() -> new ApplicationException(CREDIT_CARD_NOT_FOUND));
    }
}

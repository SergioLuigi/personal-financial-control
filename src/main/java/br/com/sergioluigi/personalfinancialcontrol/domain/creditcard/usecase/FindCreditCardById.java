package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.FindCreditCardByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.CREDIT_CARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindCreditCardById {

    private final FindCreditCardByIdRepository findCreditCardByIdRepository;

    @Transactional
    public CreditCardModel execute(UUID id) {
        return findCreditCardByIdRepository
                .execute(id).orElseThrow(() -> new ApplicationException(CREDIT_CARD_NOT_FOUND));
    }
}

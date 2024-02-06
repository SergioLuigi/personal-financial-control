package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindCreditCardById;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.UpdateCreditCard;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateCreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCreditCardImpl implements UpdateCreditCard {

    private final UpdateCreditCardRepository updateCreditCardRepository;

    private final FindCreditCardById findCreditCardById;

    private final IsCreditCardUniqueByNameImpl isCreditCardUniqueByNameImpl;

    @Transactional
    public CreditCardModel execute(UUID id, CreditCardModel creditCardModel) {

        var creditCard = findCreditCardById.execute(id);

        isCreditCardUniqueByNameImpl.check(creditCard.getName(), creditCardModel.getName());

        creditCard.update(id, creditCardModel);

        return updateCreditCardRepository.execute(creditCard);
    }
}

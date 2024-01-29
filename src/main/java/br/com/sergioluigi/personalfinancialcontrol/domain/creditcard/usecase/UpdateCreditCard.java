package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.UpdateCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.service.IsCreditCardUniqueByNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCreditCard {

    private final UpdateCreditCardRepository updateCreditCardRepository;

    private final FindCreditCardById findCreditCardById;

    private final IsCreditCardUniqueByNameService isCreditCardUniqueByNameService;

    @Transactional
    public CreditCardModel execute(UUID id, CreditCardModel creditCardModel) {

        var creditCard = findCreditCardById.execute(id);

        isCreditCardUniqueByNameService.check(creditCard.getName(), creditCardModel.getName());

        creditCard.update(id, creditCardModel);

        return updateCreditCardRepository.execute(creditCard);
    }
}

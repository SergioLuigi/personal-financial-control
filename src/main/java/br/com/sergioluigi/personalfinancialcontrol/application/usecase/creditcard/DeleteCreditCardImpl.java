package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.DeleteCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.DeleteCreditCard;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.FindCreditCardById;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCreditCardImpl implements DeleteCreditCard {

    private final DeleteCreditCardRepository deleteCategoryRepository;

    private final FindCreditCardById findCreditCardById;

    @Transactional
    public void execute(UUID id) {
        var creditCard = findCreditCardById.execute(id);
        deleteCategoryRepository.execute(creditCard.getId());
    }
}

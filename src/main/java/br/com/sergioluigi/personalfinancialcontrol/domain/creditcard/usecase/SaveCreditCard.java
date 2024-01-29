package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.SaveCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.service.IsCreditCardUniqueByNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveCreditCard {

    private final SaveCreditCardRepository saveCreditCardRepository;

    private final IsCreditCardUniqueByNameService isCreditCardUniqueByNameService;

    public CreditCardModel execute(CreditCardModel creditCardModel) {

        isCreditCardUniqueByNameService.check(creditCardModel.getName());

        return saveCreditCardRepository.execute(creditCardModel);
    }
}

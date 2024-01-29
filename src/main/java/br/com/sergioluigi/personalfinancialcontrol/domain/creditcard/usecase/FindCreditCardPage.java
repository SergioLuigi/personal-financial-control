package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.FindCreditCardPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCreditCardPage {

    private final FindCreditCardPageRepository findCreditCardPageRepository;

    public Page<CreditCardModel> execute(PageRequest pageRequest) {
        return findCreditCardPageRepository.execute(pageRequest);
    }
}

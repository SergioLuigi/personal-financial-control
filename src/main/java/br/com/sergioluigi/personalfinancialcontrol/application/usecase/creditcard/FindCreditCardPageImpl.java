package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.FindCreditCardPage;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindCreditCardPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCreditCardPageImpl implements FindCreditCardPage {

    private final FindCreditCardPageRepository findCreditCardPageRepository;

    @Override
    public Page<CreditCardModel> execute(PageRequest pageRequest) {
        return findCreditCardPageRepository.execute(pageRequest);
    }
}

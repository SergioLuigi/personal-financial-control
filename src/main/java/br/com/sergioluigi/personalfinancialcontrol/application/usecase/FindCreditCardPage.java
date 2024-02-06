package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindCreditCardPage {
    Page<CreditCardModel> execute(PageRequest pageRequest);
}

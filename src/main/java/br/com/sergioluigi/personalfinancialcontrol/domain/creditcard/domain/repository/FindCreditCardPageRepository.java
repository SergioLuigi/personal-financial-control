package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindCreditCardPageRepository {
    Page<CreditCardModel> execute(PageRequest pageRequest);
}

package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindCreditCardPageRepository {
    Page<CreditCardModel> execute(PageRequest pageRequest);
}

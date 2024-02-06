package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAccountPageRepository {
    Page<AccountModel> execute(String username, Pageable pageable);
}

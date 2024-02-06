package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAccountPage {
    Page<AccountModel> execute(String username, Pageable pageable);
}

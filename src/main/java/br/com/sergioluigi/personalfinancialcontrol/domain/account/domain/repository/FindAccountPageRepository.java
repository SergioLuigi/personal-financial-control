package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAccountPageRepository {
    Page<AccountModel> execute(String username, Pageable pageable);
}

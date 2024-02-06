package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

public interface UpdateAccountRepository {
    AccountModel execute(AccountModel accountModel);
}

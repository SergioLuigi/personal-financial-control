package br.com.sergioluigi.personalfinancialcontrol.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

public interface CreateAccount {
    AccountModel execute(String name, AccountModel accountModel);
}

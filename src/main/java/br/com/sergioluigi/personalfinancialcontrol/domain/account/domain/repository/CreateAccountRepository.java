package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;

public interface CreateAccountRepository {
    AccountModel execute(AccountModel accountModel);
}
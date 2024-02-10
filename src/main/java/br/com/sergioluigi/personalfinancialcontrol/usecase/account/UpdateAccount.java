package br.com.sergioluigi.personalfinancialcontrol.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

import java.util.Map;
import java.util.UUID;

public interface UpdateAccount {
    AccountModel execute(UUID id, AccountModel updatedAccountModel);
    AccountModel execute(UUID id, Map<String, Object> properties);
}

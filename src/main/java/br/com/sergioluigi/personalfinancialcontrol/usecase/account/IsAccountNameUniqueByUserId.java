package br.com.sergioluigi.personalfinancialcontrol.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface IsAccountNameUniqueByUserId {
    void check(String accountName, UserModel user);
}

package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface IsAccountNameUniqueByUserId {
    void check(String accountName, UserModel user);
}

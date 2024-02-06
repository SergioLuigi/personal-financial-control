package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface SaveNewUserRepository {
    UserModel execute(UserModel userModel);
}

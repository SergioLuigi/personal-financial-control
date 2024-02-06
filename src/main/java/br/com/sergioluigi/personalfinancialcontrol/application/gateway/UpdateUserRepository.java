package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public interface UpdateUserRepository {
    UserModel execute(UserModel userModel);
}

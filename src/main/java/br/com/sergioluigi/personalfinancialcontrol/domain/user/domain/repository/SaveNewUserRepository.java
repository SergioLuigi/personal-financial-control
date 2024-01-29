package br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;

public interface SaveNewUserRepository {
    UserModel execute(UserModel userModel);
}

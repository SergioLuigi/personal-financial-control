package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

import java.util.Optional;

public interface FindUserByUsernameRepository {
    Optional<UserModel> execute(String username);
}

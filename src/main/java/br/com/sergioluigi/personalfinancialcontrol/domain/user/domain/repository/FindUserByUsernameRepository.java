package br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;

import java.util.Optional;
import java.util.UUID;

public interface FindUserByUsernameRepository {
    Optional<UserModel> execute(String username);
}

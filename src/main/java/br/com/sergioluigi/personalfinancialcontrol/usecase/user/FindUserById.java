package br.com.sergioluigi.personalfinancialcontrol.usecase.user;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

import java.util.UUID;

public interface FindUserById {
    UserModel execute(UUID id);
}

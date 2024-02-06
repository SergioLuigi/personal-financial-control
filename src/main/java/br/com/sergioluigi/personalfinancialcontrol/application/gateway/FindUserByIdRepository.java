package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

import java.util.UUID;

public interface FindUserByIdRepository {
    UserModel execute(UUID id);
}

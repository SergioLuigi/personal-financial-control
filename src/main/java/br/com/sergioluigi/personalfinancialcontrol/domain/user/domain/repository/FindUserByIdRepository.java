package br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;

import java.util.UUID;

public interface FindUserByIdRepository {
    UserModel execute(UUID id);
}

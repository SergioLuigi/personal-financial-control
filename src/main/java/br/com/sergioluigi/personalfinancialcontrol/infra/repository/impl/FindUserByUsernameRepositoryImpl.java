package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindUserByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.UserRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FindUserByUsernameRepositoryImpl implements FindUserByUsernameRepository {

    private final UserRepository userRepository;

    @Override
    public Optional<UserModel> execute(String username) {
        return userRepository.findByUsername(username).map(UserEntity::toModel);
    }
}

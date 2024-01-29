package br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.FindUserByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.FindUserByUsernameRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.UserRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FindUserByUsernameRepositoryImpl implements FindUserByUsernameRepository {

    private final UserRepository userRepository;

    @Override
    public Optional<UserModel> execute(String username) {
        return userRepository.findByUsername(username).map(UserEntity::toModel);
    }
}

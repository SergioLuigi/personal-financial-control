package br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.SaveNewUserRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.UserRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SaveNewUserRepositoryImpl implements SaveNewUserRepository {

    private final UserRepository userRepository;

    @Override
    public UserModel execute(UserModel userModel) {
        return userRepository.save(new UserEntity(userModel)).toModel();
    }
}

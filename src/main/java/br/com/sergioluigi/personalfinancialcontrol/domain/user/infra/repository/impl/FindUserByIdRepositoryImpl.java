package br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.FindUserByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FindUserByIdRepositoryImpl implements FindUserByIdRepository {

    private final UserRepository userRepository;

    @Override
    public UserModel execute(UUID id) {
        return userRepository.findById(id).orElseThrow(null).toModel();
    }
}

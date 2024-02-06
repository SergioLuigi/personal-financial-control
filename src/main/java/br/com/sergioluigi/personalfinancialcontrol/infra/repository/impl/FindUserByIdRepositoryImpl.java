package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindUserByIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UserRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
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

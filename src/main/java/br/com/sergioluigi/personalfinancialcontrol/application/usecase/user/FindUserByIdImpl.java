package br.com.sergioluigi.personalfinancialcontrol.application.usecase.user;

import br.com.sergioluigi.personalfinancialcontrol.usecase.user.FindUserById;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindUserByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindUserByIdImpl implements FindUserById {

    private final FindUserByIdRepository findUserByIdRepository;

    @Override
    public UserModel execute(UUID id) {
        return Optional.ofNullable(findUserByIdRepository.execute(id))
                        .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

}

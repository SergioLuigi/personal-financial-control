package br.com.sergioluigi.personalfinancialcontrol.domain.user.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.FindUserByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindUserById {

    private final FindUserByIdRepository findUserByIdRepository;

    public UserModel execute(UUID id) {
        return Optional.ofNullable(findUserByIdRepository.execute(id))
                        .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

}

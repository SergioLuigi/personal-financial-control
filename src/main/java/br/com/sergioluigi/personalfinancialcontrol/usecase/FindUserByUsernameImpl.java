package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindUserByUsername;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindUserByUsernameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindUserByUsernameImpl implements FindUserByUsername {

    private final FindUserByUsernameRepository findUserByUsernameRepository;

    @Override
    public UserModel execute(String username) {
        return findUserByUsernameRepository.execute(username)
                        .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

}

package br.com.sergioluigi.personalfinancialcontrol.domain.user.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.FindUserByUsernameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindUserByUsername {

    private final FindUserByUsernameRepository findUserByUsernameRepository;

    public UserModel execute(String username) {
        return findUserByUsernameRepository.execute(username)
                        .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

}

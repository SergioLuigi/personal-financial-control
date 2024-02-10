package br.com.sergioluigi.personalfinancialcontrol.application.usecase.user;

import br.com.sergioluigi.personalfinancialcontrol.usecase.user.UpdateUser;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserImpl implements UpdateUser {

    private final UpdateUserRepository updateUserRepository;

    @Override
    @Transactional
    public UserModel execute(UserModel userModel) {
        return updateUserRepository.execute(userModel);
    }

}

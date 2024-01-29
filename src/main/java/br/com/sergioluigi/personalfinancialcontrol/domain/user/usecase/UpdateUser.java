package br.com.sergioluigi.personalfinancialcontrol.domain.user.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.UpdateUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUser {

    private final UpdateUserRepository updateUserRepository;

    @Transactional
    public UserModel execute(UserModel userModel) {
        return updateUserRepository.execute(userModel);
    }

}

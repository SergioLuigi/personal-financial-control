package br.com.sergioluigi.personalfinancialcontrol.domain.user.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.repository.SaveNewUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveNewUser {

    private final SaveNewUserRepository saveNewUserRepository;

    @Transactional
    public void execute(UserModel userModel) {
        saveNewUserRepository.execute(userModel);
    }

}

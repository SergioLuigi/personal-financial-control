package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.SaveNewUser;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.SaveNewUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveNewUserImpl implements SaveNewUser {

    private final SaveNewUserRepository saveNewUserRepository;

    @Override
    @Transactional
    public void execute(UserModel userModel) {
        saveNewUserRepository.execute(userModel);
    }

}

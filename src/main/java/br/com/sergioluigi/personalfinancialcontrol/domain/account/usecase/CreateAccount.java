package br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.CreateAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.service.IsAccountNameUniqueByUserIdService;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.usecase.FindUserByUsername;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAccount {

    private final CreateAccountRepository createAccountRepository;
    private final IsAccountNameUniqueByUserIdService isAccountNameUniqueByUserIdService;
    private final FindUserByUsername findUserByUsername;

    @Transactional
    public AccountModel execute(String name, AccountModel accountModel) {

        var currentUser = findUserByUsername.execute(name);

        isAccountNameUniqueByUserIdService.check(accountModel.getName(), currentUser);

        accountModel.setUser(currentUser);

        return createAccountRepository.execute(accountModel);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.usecase.account.CreateAccount;
import br.com.sergioluigi.personalfinancialcontrol.usecase.user.FindUserByUsername;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.CreateAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAccountImpl implements CreateAccount {

    private final CreateAccountRepository createAccountRepository;
    private final IsAccountNameUniqueByUserIdImpl isAccountNameUniqueByUserIdImpl;
    private final FindUserByUsername findUserByUsername;

    @Override
    @Transactional
    public AccountModel execute(String name, AccountModel accountModel) {

        var currentUser = findUserByUsername.execute(name);

        isAccountNameUniqueByUserIdImpl.check(accountModel.getName(), currentUser);

        accountModel.setUser(currentUser);

        return createAccountRepository.execute(accountModel);
    }
}

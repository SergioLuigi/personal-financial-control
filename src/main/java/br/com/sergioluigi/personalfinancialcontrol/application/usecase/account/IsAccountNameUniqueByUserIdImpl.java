package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsAccountNameNotUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.IsAccountNameUniqueByUserId;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsAccountNameUniqueByUserIdImpl implements IsAccountNameUniqueByUserId {

    private final IsAccountNameNotUniqueByUserIdRepository isAccountNameNotUniqueByUserIdRepository;

    @Override
    public void check(String accountName, UserModel user) {
        if (isAccountNameNotUniqueByUserIdRepository.execute(accountName, user.getId())) {
            throw new ApplicationException(ACCOUNT_NAME_ALREADY_EXISTS);
        }
    }
}
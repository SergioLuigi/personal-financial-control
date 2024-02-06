package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.IsAccountNameUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.IsAccountNameUniqueByUserId;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsAccountNameUniqueByUserIdImpl implements IsAccountNameUniqueByUserId {

    private final IsAccountNameUniqueByUserIdRepository isAccountNameUniqueByUserIdRepository;

    @Override
    public void check(String accountName, UserModel user) {
        if (isAccountNameUniqueByUserIdRepository.execute(accountName, user.getId())) {
            throw new ApplicationException(ACCOUNT_NAME_ALREADY_EXISTS);
        }
    }
}
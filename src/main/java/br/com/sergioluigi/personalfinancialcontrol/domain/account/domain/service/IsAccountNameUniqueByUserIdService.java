package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.IsAccountNameUniqueByUserIdRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class IsAccountNameUniqueByUserIdService {

    private final IsAccountNameUniqueByUserIdRepository isAccountNameUniqueByUserIdRepository;

    public void check(String accountName, UserModel user) {
        if (!isAccountNameUniqueByUserIdRepository.execute(accountName, user.getId())) {
            throw new ApplicationException(ACCOUNT_NAME_ALREADY_EXISTS);
        }
    }
}
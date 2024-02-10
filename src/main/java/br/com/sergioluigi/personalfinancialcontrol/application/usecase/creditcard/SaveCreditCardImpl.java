package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.usecase.account.FindAccountById;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.SaveCreditCard;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.SaveCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.*;

@Service
@RequiredArgsConstructor
public class SaveCreditCardImpl implements SaveCreditCard {

    private final SaveCreditCardRepository saveCreditCardRepository;

    private final IsCreditCardNotNotUniqueByNameImpl isCreditCardNotUniqueByNameImpl;

    private final FindAccountById findAccountById;

    @Override
    @Transactional
    public CreditCardModel execute(String username, CreditCardModel creditCardModel) {

        isCreditCardNotUniqueByNameImpl.check(creditCardModel.getName(), username);

        var accountId = getAccountId(creditCardModel);

        var account = findAccountById.execute(accountId);

        var accountUserUsername = getAccountUserUsername(account);

        if (!accountUserUsername.equals(username)) {
            throw new ApplicationException(ACCOUNT_DOESNT_BELONG_TO_LOGGED_USER);
        }

        creditCardModel.setAccountModel(account);

        return saveCreditCardRepository.execute(creditCardModel);
    }

    private static String getAccountUserUsername(AccountModel account) {
        return Optional.ofNullable(account.getUser())
                .map(UserModel::getUsername)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }

    private static UUID getAccountId(CreditCardModel creditCardModel) {
        return Optional.ofNullable(creditCardModel.getAccountModel())
                .map(AccountModel::getId)
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND));
    }
}

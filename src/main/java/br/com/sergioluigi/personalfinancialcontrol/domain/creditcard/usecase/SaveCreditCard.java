package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase.FindAccountById;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.SaveCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.service.IsCreditCardUniqueByNameService;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.domain.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.*;

@Service
@RequiredArgsConstructor
public class SaveCreditCard {

    private final SaveCreditCardRepository saveCreditCardRepository;

    private final IsCreditCardUniqueByNameService isCreditCardUniqueByNameService;

    private final FindAccountById findAccountById;

    @Transactional
    public CreditCardModel execute(String username, CreditCardModel creditCardModel) {

        isCreditCardUniqueByNameService.check(creditCardModel.getName(), username);

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

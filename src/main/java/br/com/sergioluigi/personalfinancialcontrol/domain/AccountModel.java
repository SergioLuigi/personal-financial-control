package br.com.sergioluigi.personalfinancialcontrol.domain;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_DOESNT_BELONG_TO_LOGGED_USER;
import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_NOT_FOUND;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {

    private UUID id;

    private String name;

    private AccountType type;

    private Double overdraftLimit;

    private Double balance;

    private UserModel user;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdateOn;

    public void update(UUID id, AccountModel accountModel) {
        this.id = id;
        this.name = accountModel.getName();
        this.overdraftLimit = accountModel.getOverdraftLimit();
        this.balance = accountModel.getBalance();
    }

    public void throwIfLoggedUserNotAccountOwner(AccountModel account, String loggedUserUsername) {

        var user = Optional.ofNullable(account.getUser())
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        if (!user.getUsername().equals(loggedUserUsername)) {
            throw new ApplicationException(ACCOUNT_DOESNT_BELONG_TO_LOGGED_USER);
        }
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.UpdateAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.service.IsAccountNameUniqueByUserIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAccount {

    private final UpdateAccountRepository updateAccountRepository;
    private final IsAccountNameUniqueByUserIdService isAccountNameUniqueByUserIdService;
    private final FindAccountById findAccountById;

    @Transactional
    public AccountModel execute(UUID id, AccountModel updatedAccountModel) {

        var account = findAccountById.execute(id);
        var owner = account.getUser();

        if (!account.getName().equals(updatedAccountModel.getName())) {
            isAccountNameUniqueByUserIdService.check(updatedAccountModel.getName(), owner.getId());
        }

        account.update(id, updatedAccountModel);

        return updateAccountRepository.execute(account);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.usecase.account.FindAccountById;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.UpdateAccount;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.AccountRequest;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.PROPERTY_DOESNT_EXIST;

@Service
@RequiredArgsConstructor
public class UpdateAccountImpl implements UpdateAccount {

    private final Validator validator;
    private final UpdateAccountRepository updateAccountRepository;
    private final IsAccountNameUniqueByUserIdImpl isAccountNameUniqueByUserIdImpl;
    private final FindAccountById findAccountById;

    @Override
    @Transactional
    public AccountModel execute(UUID id, AccountModel updatedAccountModel) {

        var account = findAccountById.execute(id);
        var owner = account.getUser();

        if (!account.getName().equals(updatedAccountModel.getName())) {
            isAccountNameUniqueByUserIdImpl.check(updatedAccountModel.getName(), owner);
        }

        account.update(id, updatedAccountModel);

        return updateAccountRepository.execute(account);
    }

    @Override
    @Transactional
    public AccountModel execute(UUID id, Map<String, Object> properties) {

        var account = findAccountById.execute(id);
        var accountRequest = new AccountRequest(account.getName(),
                account.getOverdraftLimit(),
                account.getBalance());

        properties.forEach((fieldName, newValue) -> {

            var field = Optional.ofNullable(ReflectionUtils.findField(AccountRequest.class, fieldName))
                    .orElseThrow(() -> new ApplicationException(PROPERTY_DOESNT_EXIST));

            field.setAccessible(true);

            if (fieldName.equals("name") &&
                    newValue instanceof String &&
                        !account.getName().equals(newValue.toString())) {
                isAccountNameUniqueByUserIdImpl.check(newValue.toString(), account.getUser());
            }

            ReflectionUtils.setField(field, accountRequest, newValue);
        });

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        var updatedAccount = accountRequest.toModelBasedOn(account);

        return updateAccountRepository.execute(updatedAccount);
    }
}

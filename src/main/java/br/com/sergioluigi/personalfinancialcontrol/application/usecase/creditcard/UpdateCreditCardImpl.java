package br.com.sergioluigi.personalfinancialcontrol.application.usecase.creditcard;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.UpdateCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.FindAccountById;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.FindCreditCardById;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.UpdateCreditCard;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.CreditCardRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.*;

@Service
@RequiredArgsConstructor
public class UpdateCreditCardImpl implements UpdateCreditCard {

    private final Validator validator;

    private final UpdateCreditCardRepository updateCreditCardRepository;

    private final FindCreditCardById findCreditCardById;

    private final FindAccountById findAccountById;

    private final IsCreditCardNotNotUniqueByNameImpl isCreditCardNotUniqueByNameImpl;

    @Override
    @Transactional
    public CreditCardModel execute(UUID id, CreditCardModel creditCardModel) {

        var creditCard = findCreditCardById.execute(id);

        isCreditCardNotUniqueByNameImpl.check(creditCard.getName(), creditCardModel.getName());

        creditCard.update(id, creditCardModel);

        return updateCreditCardRepository.execute(creditCard);
    }

    @Override
    @Transactional
    public CreditCardModel execute(UUID id, Map<String, Object> properties) {

        var creditCard = findCreditCardById.execute(id);
        var creditCardRequest = new CreditCardRequest(creditCard);

        properties.forEach((fieldName, newValue) -> {

            var field = Optional.ofNullable(ReflectionUtils.findField(CreditCardRequest.class, fieldName))
                    .orElseThrow(() -> new ApplicationException(PROPERTY_DOESNT_EXIST));

            field.setAccessible(true);

            setNewAccountIdIfPresent(fieldName, newValue, creditCard);

            throwIfCreditCardNotUniqueByName(fieldName, newValue, creditCard);

            setNewValue(newValue, field, creditCardRequest);
        });

        var violations = validator.validate(creditCardRequest);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        var updatedCreditCard = creditCardRequest.toModelBasedOn(creditCard);

        return updateCreditCardRepository.execute(updatedCreditCard);
    }

    private void setNewValue(Object newValue, Field field, CreditCardRequest creditCardRequest) {
        switch (field.getType().getSimpleName()) {
            case "String":
                ReflectionUtils.setField(field, creditCardRequest, newValue.toString());
                break;
            case "UUID":
                ReflectionUtils.setField(field, creditCardRequest, UUID.fromString(newValue.toString()));
                break;
            case "Double":
                ReflectionUtils.setField(field, creditCardRequest, Double.parseDouble(newValue.toString()));
                break;
            case "LocalDate":
                ReflectionUtils.setField(field, creditCardRequest, LocalDate.parse(newValue.toString()));
                break;
            default:
                throw new ApplicationException(MISMATCH_PROPERTY_VALUE_TYPE);
        }
    }

    private void setNewAccountIdIfPresent(String fieldName, Object newValue, CreditCardModel creditCard) {
        if (fieldName.equals("accountId")) {

            var account = findAccountById.execute(UUID.fromString(newValue.toString()));

            var loggedUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            account.throwIfLoggedUserNotAccountOwner(account, loggedUserUsername);

            creditCard.setAccountModel(account);
        }
    }

    private void throwIfCreditCardNotUniqueByName(String fieldName, Object newValue, CreditCardModel creditCard) {
        if (fieldName.equals("name") && newValue instanceof String &&
                !creditCard.getName().equals(newValue.toString())) {

            isCreditCardNotUniqueByNameImpl.check(newValue.toString(), creditCard.getName());
        }
    }
}

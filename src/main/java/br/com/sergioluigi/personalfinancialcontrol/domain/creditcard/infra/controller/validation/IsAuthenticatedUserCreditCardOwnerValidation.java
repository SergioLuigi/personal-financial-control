package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.controller.validation;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase.FindCreditCardById;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IsAuthenticatedUserCreditCardOwnerValidation {

    private final FindCreditCardById findCreditCardById;

    public Boolean execute(UUID id) {
        var creditCard = findCreditCardById.execute(id);
        var account = creditCard.getAccountModel();
        var user = account.getUser();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.getUsername().equals(authentication.getName());
    }
}

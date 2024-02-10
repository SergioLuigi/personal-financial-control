package br.com.sergioluigi.personalfinancialcontrol.infra.controller.validation;

import br.com.sergioluigi.personalfinancialcontrol.usecase.account.FindAccountById;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IsAuthenticatedUserAccountOwnerValidation {

    private final FindAccountById findAccountById;

    public Boolean execute(UUID id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        return authentication.isAuthenticated() &&
                findAccountById.execute(id).getUser().getUsername().equals(username);
    }

}

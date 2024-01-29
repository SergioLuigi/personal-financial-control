package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model.AccountRequest;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.model.AccountResponse;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.validation.IsAuthenticatedUserAccountOwner;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final CreateAccount createAccount;
    private final FindAccountById findAccountById;
    private final UpdateAccount updateAccount;
    private final DeleteAccount deleteAccount;
    private final FindAccountPage findAccountPage;

    @GetMapping
    @ResponseStatus(OK)
    public Page<AccountResponse> findPage(@PageableDefault Pageable pageable, Principal principal) {
        return findAccountPage.execute(principal.getName(), pageable).map(AccountResponse::new);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public AccountResponse create(
            @Valid @RequestBody AccountRequest accountRequest, Principal principal) {
        return new AccountResponse(createAccount.execute(principal.getName(), accountRequest.toModel()));
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    @IsAuthenticatedUserAccountOwner
    public AccountResponse findById(@PathVariable UUID id) {
        return new AccountResponse(findAccountById.execute(id));
    }

    @ResponseStatus(OK)
    @PutMapping("/{id}")
    @IsAuthenticatedUserAccountOwner
    public AccountResponse update(@PathVariable UUID id, @Valid @RequestBody AccountRequest accountRequest) {
        return new AccountResponse(updateAccount.execute(id, accountRequest.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @IsAuthenticatedUserAccountOwner
    public void deleteById(@PathVariable UUID id) {
        deleteAccount.execute(id);
    }
}

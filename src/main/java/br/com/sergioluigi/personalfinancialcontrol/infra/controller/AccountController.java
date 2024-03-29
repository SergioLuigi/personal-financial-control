package br.com.sergioluigi.personalfinancialcontrol.infra.controller;

import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.AccountRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.AccountResponse;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.AdjustAccountBalanceRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.AdjustAccountLimitRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.validation.IsAuthenticatedUserAccountOwner;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
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
    private final AdjustAccountBalance adjustAccountBalance;
    private final AdjustAccountLimit adjustAccountLimit;
    private final GetAddedAccountBalances getAddedAccountBalances;

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
    @GetMapping("/added-balances")
    public Double getAddedAccountBalances(Principal principal) {
        return getAddedAccountBalances.execute(principal.getName());
    }

    @ResponseStatus(OK)
    @PutMapping("/{id}")
    @IsAuthenticatedUserAccountOwner
    public AccountResponse update(@PathVariable UUID id, @Valid @RequestBody AccountRequest accountRequest) {
        return new AccountResponse(updateAccount.execute(id, accountRequest.toModel()));
    }

    @ResponseStatus(OK)
    @IsAuthenticatedUserAccountOwner
    @PutMapping("/{id}/balance-adjustment")
    public AccountResponse adjustBalance(@PathVariable UUID id,
                                         @RequestBody @Valid AdjustAccountBalanceRequest request) {
        return new AccountResponse(adjustAccountBalance.execute(id, request.amount()));
    }

    @ResponseStatus(OK)
    @IsAuthenticatedUserAccountOwner
    @PutMapping("/{id}/limit-adjustment")
    public AccountResponse adjustLimit(@PathVariable UUID id,
                                         @RequestBody @Valid AdjustAccountLimitRequest request) {
        return new AccountResponse(adjustAccountLimit.execute(id, request.amount()));
    }

    @ResponseStatus(OK)
    @PatchMapping("/{id}")
    @IsAuthenticatedUserAccountOwner
    public AccountResponse patch(@PathVariable UUID id, @RequestBody Map<String, Object> properties) {
        return new AccountResponse(updateAccount.execute(id, properties));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @IsAuthenticatedUserAccountOwner
    public void deleteById(@PathVariable UUID id) {
        deleteAccount.execute(id);
    }
}

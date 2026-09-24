package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.FindBankAccountByIdUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.FindBankAccountsPageUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.UpdateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.BankAccountPageFilterRequest;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.BankAccountResponse;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.CreateUpdateBankAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-account")
class BankAccountController {

    private final CreateBankAccountUseCase createBankAccount;

    private final UpdateBankAccountUseCase updateBankAccount;

    private final FindBankAccountByIdUseCase findBankAccountById;

    private final FindBankAccountsPageUseCase findBankAccountsPage;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BankAccountResponse create(@Valid @RequestBody CreateUpdateBankAccountRequest request) {
        return BankAccountResponse.from(createBankAccount.execute(request.toDomain()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    PagedModel<BankAccountResponse> getPage(
            @Valid BankAccountPageFilterRequest filter,
            @PageableDefault(sort = "name", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var page = findBankAccountsPage
                .execute(filter.toDomain(), pageable)
                .map(BankAccountResponse::from);

        return new PagedModel<>(page);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    BankAccountResponse findById(@PathVariable String id) {
        return BankAccountResponse.from(findBankAccountById.execute(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    BankAccountResponse update(
            @PathVariable String id,
            @Valid @RequestBody CreateUpdateBankAccountRequest request
    ) {
        return BankAccountResponse.from(updateBankAccount.execute(id, request.toDomain()));
    }
}

package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.BankAccountResponse;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.CreateBankAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-accounts")
class BankAccountController {

    private final CreateBankAccountUseCase createBankAccount;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BankAccountResponse create(@Valid @RequestBody CreateBankAccountRequest request) {
        return BankAccountResponse.from(createBankAccount.execute(request.toDomain()));
    }
}

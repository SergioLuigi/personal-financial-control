package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest;

import br.com.sergioluigi.personal_financial_control.commons.pagination.PageResponse;
import br.com.sergioluigi.personal_financial_control.commons.web.PathIds;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.GetBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.ListBankAccountsUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.UpdateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.BankAccountDetailsResponse;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.BankAccountResponse;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.CreateBankAccountRequest;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto.UpdateBankAccountRequest;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.DeleteBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.PreviewBankAccountDeletionUseCase;
import br.com.sergioluigi.personal_financial_control.commons.deletion.DeletionPreviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.math.BigDecimal;
import java.util.Map;

/** The {@code /bank-accounts} endpoints. */
@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
class BankAccountController {

    /** Creates accounts. */
    private final CreateBankAccountUseCase createBankAccountUseCase;
    /** Lists accounts. */
    private final ListBankAccountsUseCase listBankAccountsUseCase;
    /** Reads the details of an account. */
    private final GetBankAccountUseCase getBankAccountUseCase;
    /** Changes accounts. */
    private final UpdateBankAccountUseCase updateBankAccountUseCase;
    /** Deletes accounts. */
    private final DeleteBankAccountUseCase deleteBankAccountUseCase;
    /** Shows what deleting an account involves. */
    private final PreviewBankAccountDeletionUseCase previewBankAccountDeletionUseCase;

    /**
     * Creates an account.
     *
     * @param principal the authenticated user
     * @param request the account to create
     * @return the account created
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BankAccountResponse create(Principal principal, @Valid @RequestBody CreateBankAccountRequest request) {
        return BankAccountResponse.from(createBankAccountUseCase.execute(principal.getName(), request.toDomain()));
    }

    /**
     * Lists one page of accounts.
     *
     * @param principal the authenticated user
     * @param page the page number, from 0
     * @param name text the name must contain
     * @param description text the description must contain
     * @param minBalance the lowest balance accepted
     * @param maxBalance the highest balance accepted
     * @return the page of accounts
     */
    @GetMapping
    PageResponse<BankAccountResponse> list(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(name = "min_balance", required = false) BigDecimal minBalance,
            @RequestParam(name = "max_balance", required = false) BigDecimal maxBalance
    ) {
        var filter = new BankAccountFilter(name, description, minBalance, maxBalance);

        return PageResponse.from(listBankAccountsUseCase.execute(principal.getName(), filter, page), BankAccountResponse::from);
    }

    /**
     * Reads the details of an account.
     *
     * @param principal the authenticated user
     * @param id the id of the account, as written in the path
     * @param params the query parameters, handed to the sections of other modules
     * @return the account and its sections
     */
    @GetMapping("/{id}")
    BankAccountDetailsResponse get(Principal principal, @PathVariable String id, @RequestParam Map<String, String> params) {
        return BankAccountDetailsResponse.from(getBankAccountUseCase.execute(principal.getName(), PathIds.parse(id), params));
    }

    /**
     * Changes an account.
     *
     * @param principal the authenticated user
     * @param id the id of the account, as written in the path
     * @param request the fields to change
     * @return the account after the changes
     */
    @PatchMapping("/{id}")
    BankAccountResponse update(Principal principal, @PathVariable String id, @Valid @RequestBody UpdateBankAccountRequest request) {
        return BankAccountResponse.from(updateBankAccountUseCase.execute(principal.getName(), PathIds.parse(id), request.toDomain()));
    }

    /**
     * Deletes an account once the user authorized erasing earlier open months and acknowledged the warning.
     *
     * @param principal the authenticated user
     * @param id the id of the account, as written in the path
     * @param authorizeOpenPast whether the user authorized erasing entries of earlier open months
     * @param acknowledgeWarnings whether the user acknowledged the warning of the deletion
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            Principal principal,
            @PathVariable String id,
            @RequestParam(name = "authorize_open_past", defaultValue = "false") boolean authorizeOpenPast,
            @RequestParam(name = "acknowledge_warnings", defaultValue = "false") boolean acknowledgeWarnings
    ) {
        deleteBankAccountUseCase.execute(principal.getName(), PathIds.parse(id), authorizeOpenPast, acknowledgeWarnings);
    }

    /**
     * What deleting the account involves, without deleting anything.
     *
     * @param principal the authenticated user
     * @param id the id of the account, as written in the path
     * @return the preview
     */
    @GetMapping("/{id}/deletion-preview")
    DeletionPreviewResponse previewDeletion(Principal principal, @PathVariable String id) {
        return DeletionPreviewResponse.from(previewBankAccountDeletionUseCase.execute(principal.getName(), PathIds.parse(id)));
    }
}

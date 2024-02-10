package br.com.sergioluigi.personalfinancialcontrol.infra.controller;

import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.CreditCardRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.model.CreditCardResponse;
import br.com.sergioluigi.personalfinancialcontrol.infra.controller.validation.IsAuthenticatedUserCreditCardOwner;
import br.com.sergioluigi.personalfinancialcontrol.usecase.creditcard.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/credit-card")
public class CreditCardController {

    private final SaveCreditCard saveCreditCard;
    private final UpdateCreditCard updateCreditCard;
    private final FindCreditCardById findCreditCardById;
    private final FindCreditCardPage findCreditCardPage;
    private final DeleteCreditCard deleteCreditCard;

    @GetMapping
    @ResponseStatus(OK)
    public Page<CreditCardResponse> findPage(@PageableDefault Pageable pageable) {
        return this.findCreditCardPage
                .execute(PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(), pageable.getSort()))
                .map(CreditCardResponse::new);
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}")
    @IsAuthenticatedUserCreditCardOwner
    public CreditCardResponse findById(@PathVariable UUID id) {
        return new CreditCardResponse(this.findCreditCardById.execute(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreditCardResponse create(
            @Valid @RequestBody CreditCardRequest creditCardRequest,
            Principal principal) {
        return new CreditCardResponse(this.saveCreditCard.execute(principal.getName(), creditCardRequest.toModel()));
    }

    @ResponseStatus(OK)
    @PutMapping("/{id}")
    @IsAuthenticatedUserCreditCardOwner
    public CreditCardResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreditCardRequest creditCardRequest) {
        return new CreditCardResponse(this.updateCreditCard.execute(id, creditCardRequest.toModel()));
    }

    @ResponseStatus(OK)
    @PatchMapping("/{id}")
    @IsAuthenticatedUserCreditCardOwner
    public CreditCardResponse patch(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> properties) {
        return new CreditCardResponse(this.updateCreditCard.execute(id, properties));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @IsAuthenticatedUserCreditCardOwner
    public void delete(@PathVariable UUID id) {
        this.deleteCreditCard.execute(id);
    }
}

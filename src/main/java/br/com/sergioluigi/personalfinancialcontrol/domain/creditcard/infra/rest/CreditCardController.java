package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.rest;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.rest.model.CreditCardRequest;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.rest.model.CreditCardResponse;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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
    public CreditCardResponse findById(@PathVariable UUID id) {
        return new CreditCardResponse(this.findCreditCardById.execute(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CreditCardResponse create(@RequestBody CreditCardRequest creditCardRequest) {
        return new CreditCardResponse(this.saveCreditCard.execute(creditCardRequest.toModel()));
    }

    @ResponseStatus(OK)
    @PutMapping("/{id}")
    public CreditCardResponse update(
            @PathVariable UUID id,
            @RequestBody CreditCardRequest creditCardRequest) {
        return new CreditCardResponse(this.updateCreditCard.execute(id, creditCardRequest.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        this.deleteCreditCard.execute(id);
    }

}

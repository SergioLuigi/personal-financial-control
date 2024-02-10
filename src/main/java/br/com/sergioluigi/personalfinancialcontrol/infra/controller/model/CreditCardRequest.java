package br.com.sergioluigi.personalfinancialcontrol.infra.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.CreditCardModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    @Size(min = 3, max = 150)
    private String name;

    @NotNull
    private Double limit;

    @NotNull
    private Double balance;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private LocalDate closingDate;

    public CreditCardRequest(CreditCardModel creditCardModel) {
        this.accountId = Optional.ofNullable(creditCardModel.getAccountModel())
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND)).getId();
        this.name = creditCardModel.getName();
        this.limit = creditCardModel.getLimit();
        this.balance = creditCardModel.getBalance();
        this.dueDate = creditCardModel.getDueDate();
        this.closingDate = creditCardModel.getClosingDate();
    }

    public CreditCardModel toModel() {
        return CreditCardModel.builder()
                .name(name)
                .limit(limit)
                .balance(balance)
                .dueDate(dueDate)
                .accountModel(AccountModel.builder()
                        .id(accountId).build())
                .closingDate(closingDate)
                .build();
    }

    public CreditCardModel toModelBasedOn(CreditCardModel creditCardModel) {
        return CreditCardModel.builder()
                .id(creditCardModel.getId())
                .name(name)
                .limit(limit)
                .balance(balance)
                .dueDate(dueDate)
                .accountModel(creditCardModel.getAccountModel())
                .closingDate(closingDate)
                .createdOn(creditCardModel.getCreatedOn())
                .lastUpdateOn(creditCardModel.getLastUpdateOn())
                .build();
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreditCardModel {

    private UUID id;

    private AccountModel accountModel;

    private String name;

    private Double limit;

    private Double balance;

    private LocalDate dueDate;

    private LocalDate closingDate;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdateOn;

    public void update(UUID id, CreditCardModel creditCardModel) {
        this.id = id;
        this.name = creditCardModel.getName();
        this.limit = creditCardModel.getLimit();
        this.balance = creditCardModel.getBalance();
        this.dueDate = creditCardModel.getDueDate();
        this.closingDate = creditCardModel.closingDate;
        this.createdOn = creditCardModel.createdOn;
        this.lastUpdateOn = creditCardModel.getLastUpdateOn();
    }
}

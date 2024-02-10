package br.com.sergioluigi.personalfinancialcontrol.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
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
        this.lastUpdateOn = creditCardModel.getLastUpdateOn();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCardModel that = (CreditCardModel) o;
        return Objects.equals(accountModel, that.accountModel) && Objects.equals(name, that.name) && Objects.equals(limit, that.limit) && Objects.equals(balance, that.balance) && Objects.equals(dueDate, that.dueDate) && Objects.equals(closingDate, that.closingDate) && Objects.equals(createdOn, that.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountModel, name, limit, balance, dueDate, closingDate, createdOn);
    }
}

package br.com.sergioluigi.personalfinancialcontrol.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {

    private UUID id;

    private String name;

    private Double overdraftLimit;

    private Double balance;

    private UserModel user;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdateOn;

    public void update(UUID id, AccountModel accountModel) {
        this.id = id;
        this.name = accountModel.getName();
        this.overdraftLimit = accountModel.getOverdraftLimit();
        this.balance = accountModel.getBalance();
    }
}

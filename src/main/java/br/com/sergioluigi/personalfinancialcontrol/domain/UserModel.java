package br.com.sergioluigi.personalfinancialcontrol.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
public class UserModel {

    private UUID id;

    private String name;

    private String username;

    private String email;

    private String cpf;

    private SubscriptionType subscriptionType;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdateOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(name, userModel.name) && Objects.equals(username, userModel.username) && Objects.equals(email, userModel.email) && Objects.equals(cpf, userModel.cpf) && subscriptionType == userModel.subscriptionType && Objects.equals(createdOn, userModel.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, username, email, cpf, subscriptionType, createdOn);
    }
}

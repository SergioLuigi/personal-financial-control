package br.com.sergioluigi.personalfinancialcontrol.domain.user.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
}

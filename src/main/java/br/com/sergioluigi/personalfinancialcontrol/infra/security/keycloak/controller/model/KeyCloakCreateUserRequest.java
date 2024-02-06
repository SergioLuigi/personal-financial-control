package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public record KeyCloakCreateUserRequest(
        String username,
        String name,
        String cpf,
        String email,
        String password
) {
    public UserModel toUserModel() {
        return UserModel.builder()
                .id(null)
                .username(username)
                .name(name)
                .email(email)
                .cpf(cpf)
                .build();
    }
}

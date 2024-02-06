package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.controller.model;

import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;

public record UpdateKeyCloakUserProfileRequest(
        String name
) {

    public UserModel toUserModel() {
        return UserModel.builder()
                .name(name)
                .build();
    }
}

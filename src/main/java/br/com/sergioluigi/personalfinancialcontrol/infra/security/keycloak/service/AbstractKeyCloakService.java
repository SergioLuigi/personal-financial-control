package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
public abstract class AbstractKeyCloakService {

    private final Keycloak keycloak;

    @Value("${spring.security.oauth2.client.registration.keycloak.realm}")
    private String realmName;

    public RealmResource getRealm() {
        return getKeycloak().realm(this.getRealmName());
    }

    public RolesResource getRealRoles() {
        return getRealm().roles();
    }

    public UserRepresentation getKeycloakUserByUsername(String username) {

        var keycloakUser = getRealm().users().search(username, true);

        if (!keycloakUser.isEmpty()) {
            return keycloakUser.get(0);
        }

        return null;
    }
}

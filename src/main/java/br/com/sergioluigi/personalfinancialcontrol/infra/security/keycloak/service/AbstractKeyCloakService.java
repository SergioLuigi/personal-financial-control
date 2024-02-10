package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public UsersResource getRealmUsers() {
        return getRealm().users();
    }

    public Optional<UserRepresentation> getKeycloakUserByUsername(String username) {
        return getRealmUsers().searchByUsername(username, true).stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}

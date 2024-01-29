package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {

    @Value("${spring.security.oauth2.client.registration.keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String client;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .username(client)
                .clientId(client)
                .password(clientSecret)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .scope(OAuth2Constants.SCOPE_OPENID)
                .build();
    }
}

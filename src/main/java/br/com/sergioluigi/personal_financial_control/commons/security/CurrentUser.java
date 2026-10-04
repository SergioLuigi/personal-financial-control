package br.com.sergioluigi.personal_financial_control.commons.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The user making the current request, read from the Spring Security context. Every query is scoped to it.
 */
@Component
public class CurrentUser {

    /**
     * The name of the authenticated user.
     *
     * @return the username
     * @throws IllegalStateException when no user is authenticated
     */
    public String getUsername() {
        return findUsername().orElseThrow(
                () -> new IllegalStateException("No authenticated user in the security context"));
    }

    /**
     * The name of the authenticated user, if any. Anonymous requests count as no user.
     *
     * @return the username, or empty when no user is authenticated
     */
    public Optional<String> findUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken))
                .map(Authentication::getName);
    }
}

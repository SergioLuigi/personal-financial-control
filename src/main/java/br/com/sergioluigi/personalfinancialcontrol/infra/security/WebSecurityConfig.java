package br.com.sergioluigi.personalfinancialcontrol.infra.security;

import br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.config.KeyCloakGrantedAuthorityConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final String AUTH_USER_REGISTER_ENDPOINT = "/auth/user";

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(getSessionManagementConfigurerCustomizer())
                .authorizeHttpRequests(request ->
                        request.requestMatchers(HttpMethod.POST, AUTH_USER_REGISTER_ENDPOINT).permitAll()
                                .requestMatchers(HttpMethod.POST, "/error").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                       jwt.jwtAuthenticationConverter(getAuthenticationConverter())))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @NotNull
    private static Customizer<SessionManagementConfigurer<HttpSecurity>> getSessionManagementConfigurerCustomizer() {
        return sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private JwtAuthenticationConverter getAuthenticationConverter() {
        var authenticationConverter = new JwtAuthenticationConverter();
        var converter = new KeyCloakGrantedAuthorityConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        authenticationConverter.setPrincipalClaimName("preferred_username");
        return authenticationConverter;
    }
}

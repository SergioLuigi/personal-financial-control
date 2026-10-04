package br.com.sergioluigi.personal_financial_control.commons.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Security setup: stateless HTTP Basic authentication, with only the health endpoint open. */
@Configuration
class SecurityConfig {

    /**
     * Requires authentication on everything but {@code /actuator/health}; no CSRF and no session, since
     * every request carries its own credentials.
     *
     * @param http the builder of the filter chain
     * @return the filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * The encoder for passwords, which also recognizes the prefix of each stored hash.
     *
     * @return the delegating encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * The only user of the application, kept in memory: sign-up and user management are out of scope.
     *
     * @param passwordEncoder encodes the password of the user
     * @return the user store
     */
    @Bean
    InMemoryUserDetailsManager userDetailsManager(
            PasswordEncoder passwordEncoder
    ) {
        var user = User.withUsername("user")
                .password("password")
                .passwordEncoder(passwordEncoder::encode)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}

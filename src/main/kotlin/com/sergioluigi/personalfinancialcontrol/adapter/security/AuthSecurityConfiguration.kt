package com.sergioluigi.personalfinancialcontrol.adapter.security

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.CognitoAuthenticationToken
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class AuthSecurityConfiguration: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/users/login").permitAll()
            .antMatchers(HttpMethod.POST, "/users/signup").permitAll()
            .antMatchers(HttpMethod.POST, "/users/confirm-self-signup").permitAll()
            .antMatchers(HttpMethod.POST, "/users/resend-confirmation-code").permitAll()
            .and()
            .authorizeRequests().antMatchers("/**").authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter{
                CognitoAuthenticationToken(it)
            }
    }
}
package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito

import com.sergioluigi.personalfinancialcontrol.core.domain.exception.PersonalFinancialControlError
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import trueOrThrow
import java.time.Instant

class CognitoAuthenticationToken(
    val jwt: Jwt,
    grantedAuthorities: Collection<GrantedAuthority>?
): JwtAuthenticationToken(jwt, grantedAuthorities),
    Converter<Jwt, JwtAuthenticationToken> {

    constructor(jwt: Jwt): this(
        jwt,
        jwt.getClaimAsStringList("cognito:groups").map { SimpleGrantedAuthority("ROLE_$it") }
    ){
        validateTokenExpiration(jwt)
    }

    override fun convert(source: Jwt): JwtAuthenticationToken = this

    private fun validateTokenExpiration(jwt: Jwt) =
        jwt.getClaim<Instant>(JwtClaimNames.EXP).isAfter(Instant.now()).trueOrThrow(PersonalFinancialControlError.EXPIRED_TOKEN)

}
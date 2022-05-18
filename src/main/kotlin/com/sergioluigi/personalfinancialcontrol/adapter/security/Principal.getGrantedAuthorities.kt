package com.sergioluigi.personalfinancialcontrol.adapter.security

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.CognitoAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.Principal

fun Principal.getGrantedAuthorities(): List<SimpleGrantedAuthority> = (this as CognitoAuthenticationToken).jwt.getClaimAsStringList("cognito:groups").map { SimpleGrantedAuthority("ROLE_$it") }
package com.sergioluigi.personalfinancialcontrol.adapter.security.extension


import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.CognitoAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.Principal

fun Principal.getCPF(): String = (this as CognitoAuthenticationToken).jwt.getClaim("username")
fun Principal.getGrantedAuthorities(): List<SimpleGrantedAuthority> = (this as CognitoAuthenticationToken).jwt.getClaimAsStringList("cognito:groups").map { SimpleGrantedAuthority("ROLE_$it") }
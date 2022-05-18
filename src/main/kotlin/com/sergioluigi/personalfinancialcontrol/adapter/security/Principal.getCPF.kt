package com.sergioluigi.personalfinancialcontrol.adapter.security


import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.CognitoAuthenticationToken
import java.security.Principal

fun Principal.getCPF(): String = (this as CognitoAuthenticationToken).jwt.getClaim("username")
package com.sergioluigi.personalfinancialcontrol.adapter.security.dto

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.fasterxml.jackson.annotation.JsonProperty

class AcessTokenDTO(
    @JsonProperty("acess_token")
    val acessToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val refreshToken: String,
){
    constructor(authenticationResultType: AuthenticationResultType): this(
        authenticationResultType.accessToken,
        authenticationResultType.expiresIn,
        authenticationResultType.tokenType,
        authenticationResultType.refreshToken,
    )
}
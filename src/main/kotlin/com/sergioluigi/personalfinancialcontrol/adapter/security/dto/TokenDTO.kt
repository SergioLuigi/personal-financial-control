package com.sergioluigi.personalfinancialcontrol.adapter.security.dto

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType
import com.fasterxml.jackson.annotation.JsonProperty

class TokenDTO(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,
){
    constructor(authenticationResultType: AuthenticationResultType): this(
        authenticationResultType.accessToken,
        authenticationResultType.expiresIn,
        authenticationResultType.tokenType,
        authenticationResultType.refreshToken,
    )
}
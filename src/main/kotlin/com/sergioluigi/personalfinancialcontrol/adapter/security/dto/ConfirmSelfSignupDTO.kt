package com.sergioluigi.personalfinancialcontrol.adapter.security.dto

import javax.validation.constraints.NotBlank

data class ConfirmSelfSignupDTO(

    @field:NotBlank
    val confirmationCode: String,

    @field:NotBlank
    val username: String
)
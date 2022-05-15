package com.sergioluigi.personalfinancialcontrol.adapter.security.dto

import javax.validation.constraints.NotBlank

data class ResendConfirmationCode(
    @field:NotBlank
    val username: String
)
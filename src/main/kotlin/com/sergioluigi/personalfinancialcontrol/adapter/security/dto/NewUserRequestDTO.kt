package com.sergioluigi.personalfinancialcontrol.adapter.security.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.Group
import org.hibernate.validator.constraints.br.CPF
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class NewUserRequestDTO(

    @JsonProperty("profile")
    val groups: Set<String> = setOf(Group.REGULAR.name),

    @CPF
    @NotBlank
    @JsonProperty("cpf")
    val cpf: String,

    @NotBlank
    @JsonProperty("email")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
    val email: String,

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
    @JsonProperty("password")
    val password: String,

    @NotNull
    @JsonProperty("birthDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDate: LocalDate,

    @JsonProperty("phoneNumber")
    val phoneNumber: String,

    @NotBlank
    @JsonProperty("cellPhoneNumber")
    val cellPhoneNumber: String,

    @NotBlank
    @JsonProperty("name")
    val name: String
)
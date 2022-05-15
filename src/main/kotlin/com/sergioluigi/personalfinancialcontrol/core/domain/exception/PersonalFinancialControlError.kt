package com.sergioluigi.personalfinancialcontrol.core.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class PersonalFinancialControlError(val httpStatus: HttpStatus, val reason: String) {
    EXPIRED_TOKEN(UNAUTHORIZED,"expired.token"),
    CHANGE_PASSWORD_REQUIRED(BAD_REQUEST,"change.password.required"),
    INVALID_USERNAME_OR_PASSWORD(INTERNAL_SERVER_ERROR, "invalid.username.or.password"),
    //ERROR_TO_PERFORM_PASSWORD_CHANGE()
}
package com.sergioluigi.personalfinancialcontrol.core.domain.exception

import org.springframework.web.server.ResponseStatusException

data class PersonalFinancialControlException(val error: PersonalFinancialControlError): ResponseStatusException(error.httpStatus,error.reason)

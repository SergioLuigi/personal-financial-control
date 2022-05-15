package com.sergioluigi.personalfinancialcontrol.core.domain.exception.extension

import com.sergioluigi.personalfinancialcontrol.core.domain.exception.PersonalFinancialControlError
import org.springframework.web.server.ResponseStatusException

/**
 * Throw exception if the source value is equal to false
 */
fun Boolean.falseOrThrow(error: PersonalFinancialControlError) =
	when(this){
		false -> false
		else -> throw ResponseStatusException(error.httpStatus,error.reason)
	}
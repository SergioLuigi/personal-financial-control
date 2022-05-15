import com.sergioluigi.personalfinancialcontrol.core.domain.exception.PersonalFinancialControlError
import org.springframework.web.server.ResponseStatusException

/**
 * Throw exception if the source value is equal to true
 */
fun Boolean.trueOrThrow(error: PersonalFinancialControlError) =
	when(this){
		true -> true
		else -> throw ResponseStatusException(error.httpStatus,error.reason)
	}


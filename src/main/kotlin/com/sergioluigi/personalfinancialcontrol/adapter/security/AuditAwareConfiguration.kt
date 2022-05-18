package com.sergioluigi.personalfinancialcontrol.adapter.security

import com.sergioluigi.personalfinancialcontrol.adapter.database.entity.User
import com.sergioluigi.personalfinancialcontrol.adapter.database.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
class AuditAwareConfiguration {

	@Bean
	fun auditAware(userRepository: UserRepository): AuditorAware<User> =
		AuditorAware {
			val authentication = SecurityContextHolder.getContext().authentication
			if(authentication == null || !authentication.isAuthenticated || authentication is AnonymousAuthenticationToken){
				Optional.empty()
			}else{
				Optional.ofNullable(userRepository.findByCpf(authentication.getCPF()))
			}
		}

}
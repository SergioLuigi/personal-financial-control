package br.com.sergioluigi.personal_financial_control.commons.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Turns on JPA auditing, using {@link CurrentAuditorProvider} to name the author of each write. */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "currentAuditorProvider")
class JpaAuditingConfig {
}

package br.com.sergioluigi.personal_financial_control.commons.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "currentAuditorProvider")
class JpaAuditingConfig {
}

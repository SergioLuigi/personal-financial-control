package br.com.sergioluigi.personal_financial_control.commons.audit;

import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Tells Spring Data auditing who is writing: the authenticated user, or {@code "system"} when no one is
 * authenticated, as in the automated processes.
 */
@Component
@RequiredArgsConstructor
public class CurrentAuditorProvider implements AuditorAware<String> {

    /** The auditor recorded for writes made outside a user request. */
    static final String SYSTEM_AUDITOR = "system";

    /** Source of the authenticated username. */
    private final CurrentUser currentUser;

    /** Returns the authenticated username, or {@link #SYSTEM_AUDITOR} when there is none. */
    @Override
    public Optional<String> getCurrentAuditor() {
        return currentUser.findUsername().or(() -> Optional.of(SYSTEM_AUDITOR));
    }
}

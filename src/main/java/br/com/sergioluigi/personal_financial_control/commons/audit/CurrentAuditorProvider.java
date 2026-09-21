package br.com.sergioluigi.personal_financial_control.commons.audit;

import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentAuditorProvider implements AuditorAware<String> {

    private final CurrentUser currentUser;

    @Override
    public Optional<String> getCurrentAuditor() {
        return currentUser.findUsername();
    }
}

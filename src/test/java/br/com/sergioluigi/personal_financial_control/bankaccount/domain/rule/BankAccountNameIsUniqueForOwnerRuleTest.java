package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BankAccountNameIsUniqueForOwnerRuleTest {

    private final BankAccountRepository repository = mock(BankAccountRepository.class);
    private final BankAccountNameIsUniqueForOwnerRule rule = new BankAccountNameIsUniqueForOwnerRule(repository);

    @Test
    void acceptsANameTheOwnerDoesNotUse() {
        when(repository.existsByOwnerAndName("alice", "savings")).thenReturn(false);

        assertThatCode(() -> rule.check("alice", "savings")).doesNotThrowAnyException();
    }

    @Test
    void rejectsANameTheOwnerAlreadyUses() {
        when(repository.existsByOwnerAndName("alice", "savings")).thenReturn(true);

        assertThatThrownBy(() -> rule.check("alice", "savings"))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getBusinessMessage()).isEqualTo(BankAccountMessage.NAME_ALREADY_EXISTS));
    }
}

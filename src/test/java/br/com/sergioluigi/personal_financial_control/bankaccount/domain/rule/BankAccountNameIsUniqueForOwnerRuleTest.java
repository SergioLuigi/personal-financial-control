package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class BankAccountNameIsUniqueForOwnerRuleTest {

    private final BankAccountRepository bankAccountRepository = mock(BankAccountRepository.class);

    private final BankAccountNameIsUniqueForOwnerRule rule = new BankAccountNameIsUniqueForOwnerRule(bankAccountRepository);

    @Test
    void passesWhenTheOwnerHasNoAccountWithThatName() {
        given(bankAccountRepository.existsByOwnerAndName("alice", "savings")).willReturn(false);

        assertThatNoException().isThrownBy(() -> rule.check("alice", "savings"));
    }

    @Test
    void failsWhenTheOwnerAlreadyHasAnAccountWithThatName() {
        given(bankAccountRepository.existsByOwnerAndName("alice", "savings")).willReturn(true);

        assertThatThrownBy(() -> rule.check("alice", "savings"))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getBusinessMessage()).isEqualTo(NAME_ALREADY_EXISTS));
    }
}

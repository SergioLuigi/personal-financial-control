package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountBalanceRangeIsValidRuleTest {

    private final BankAccountBalanceRangeIsValidRule rule = new BankAccountBalanceRangeIsValidRule();

    @Test
    void acceptsEqualBoundsAndOpenRanges() {
        assertThatCode(() -> rule.check(BigDecimal.ONE, BigDecimal.ONE)).doesNotThrowAnyException();
        assertThatCode(() -> rule.check(null, BigDecimal.ONE)).doesNotThrowAnyException();
        assertThatCode(() -> rule.check(BigDecimal.ONE, null)).doesNotThrowAnyException();
    }

    @Test
    void rejectsAMaximumBelowTheMinimum() {
        assertThatThrownBy(() -> rule.check(BigDecimal.TEN, BigDecimal.ONE)).isInstanceOf(BusinessException.class);
    }
}

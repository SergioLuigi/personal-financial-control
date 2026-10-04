package br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** The balance range of a filter must not end below where it starts. */
@Component
public class BankAccountBalanceRangeIsValidRule {

    /**
     * Checks the range.
     *
     * @param minBalance the lowest balance, or {@code null}
     * @param maxBalance the highest balance, or {@code null}
     * @throws BusinessException when both are given and the maximum is lower than the minimum
     */
    public void check(@Nullable BigDecimal minBalance, @Nullable BigDecimal maxBalance) {
        if (minBalance != null && maxBalance != null && maxBalance.compareTo(minBalance) < 0) {
            throw new BusinessException(BankAccountMessage.INVALID_BALANCE_RANGE);
        }
    }
}

package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountTest {

    private final BankAccount account =
            new BankAccount(UUID.randomUUID(), "alice", "savings", "main", new BigDecimal("10.00"));

    @Test
    void appliesOnlyTheSuppliedFields() {
        var updated = account.apply(new BankAccountChanges(" Checking ", null, null));

        assertThat(updated.name()).isEqualTo("checking");
        assertThat(updated.description()).isEqualTo("main");
        assertThat(updated.balance()).isEqualByComparingTo("10.00");
    }

    @Test
    void anEmptyDescriptionClearsIt() {
        assertThat(account.apply(new BankAccountChanges(null, "", null)).description()).isEmpty();
    }

    @Test
    void noChangesLeaveTheAccountEqual() {
        assertThat(account.apply(new BankAccountChanges(null, null, null))).isEqualTo(account);
    }
}

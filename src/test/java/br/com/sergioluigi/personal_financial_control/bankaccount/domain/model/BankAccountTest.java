package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountTest {

    @Test
    void balanceDefaultsToZeroWhenAbsent() {
        var bankAccount = BankAccount.create("alice", new NewBankAccount("savings", null, null));

        assertThat(bankAccount.balance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void keepsTheGivenBalanceWithTwoDecimalPlaces() {
        var bankAccount = BankAccount.create("alice", new NewBankAccount("savings", null, new BigDecimal("-150")));

        assertThat(bankAccount.balance()).isEqualTo(new BigDecimal("-150.00"));
    }

    @Test
    void belongsToTheGivenOwnerAndGetsAnId() {
        var bankAccount = BankAccount.create("alice", new NewBankAccount("savings", "Emergency fund", null));

        assertThat(bankAccount.owner()).isEqualTo("alice");
        assertThat(bankAccount.id()).isNotNull();
        assertThat(bankAccount.description()).isEqualTo("Emergency fund");
    }

    @Test
    void nameIsTrimmedAndLowercasedKeepingAccents() {
        var newBankAccount = new NewBankAccount("  Poupança ", null, null);

        assertThat(newBankAccount.name()).isEqualTo("poupança");
    }
}

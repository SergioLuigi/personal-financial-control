package br.com.sergioluigi.personal_financial_control.bankaccount.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NewBankAccountTest {

    @Test
    void trimsAndLowercasesTheNameKeepingAccents() {
        var account = new NewBankAccount(" Poupança ", null, null);

        assertThat(account.name()).isEqualTo("poupança");
    }

    @Test
    void defaultsTheBalanceToZero() {
        var account = new NewBankAccount("savings", null, null);

        assertThat(account.balance()).isEqualByComparingTo("0.00");
    }

    @Test
    void keepsANegativeBalance() {
        var account = new NewBankAccount("savings", null, new BigDecimal("-150.00"));

        assertThat(account.balance()).isEqualByComparingTo("-150.00");
    }
}

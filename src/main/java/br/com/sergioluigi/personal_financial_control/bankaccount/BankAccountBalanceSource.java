package br.com.sergioluigi.personal_financial_control.bankaccount;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * What a module adds to, or takes off, a bank account's balance — receipts add, Pix and bill
 * payments take off. The balance is what the account was stored with (its initial balance and
 * the adjustments the user made) plus the sum of every source, so each module that moves money
 * on an account implements this.
 *
 * Implemented by the income module (the receipts) and the expense module (the Pix and internal transfers).
 * The bank account module calls every implementation to calculate the balance an account shows.
 */
public interface BankAccountBalanceSource {

    /**
     * The signed amount this source contributes to the account's balance.
     *
     * @param owner the user who owns the account
     * @param bankAccountId the id of the account
     * @return the amount to add to the balance; negative when the source takes money off
     */
    BigDecimal amount(String owner, UUID bankAccountId);
}

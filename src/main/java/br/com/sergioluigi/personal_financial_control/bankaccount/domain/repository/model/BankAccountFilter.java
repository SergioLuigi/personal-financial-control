package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * The criteria of the bank accounts list; a criterion that is {@code null} does not filter.
 *
 * @param name text the name must contain
 * @param description text the description must contain
 * @param minBalance the lowest balance accepted
 * @param maxBalance the highest balance accepted
 */
public record BankAccountFilter(
        @Nullable String name,
        @Nullable String description,
        @Nullable BigDecimal minBalance,
        @Nullable BigDecimal maxBalance
) {

    /** Treats blank text as no criterion and trims the rest. */
    public BankAccountFilter {
        name = trimToNull(name);
        description = trimToNull(description);
    }

    /**
     * Trims the text, or returns {@code null} when it is absent or blank.
     *
     * @param value the text to trim
     * @return the trimmed text, or {@code null}
     */
    private static @Nullable String trimToNull(@Nullable String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}

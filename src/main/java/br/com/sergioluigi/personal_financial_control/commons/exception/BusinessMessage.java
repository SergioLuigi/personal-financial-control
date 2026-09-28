package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

/**
 * A business rule violation, described by the module whose rule was violated.
 * Each module keeps its messages in its domain, usually as an enum.
 */
public interface BusinessMessage {

    HttpStatus status();

    /**
     * The request field the violation refers to, or {@code null} when it concerns
     * the request as a whole.
     */
    @Nullable String field();

    String message();
}

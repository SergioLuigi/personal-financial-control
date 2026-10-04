package br.com.sergioluigi.personal_financial_control.bankaccount.domain.message;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessMessage;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

/** The business messages of bank accounts. */
public enum BankAccountMessage implements BusinessMessage {

    /** The owner already has an account with that name. */
    NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "name", "Bank account name already exists"),
    /** The account does not exist or belongs to another user. */
    NOT_FOUND(HttpStatus.NOT_FOUND, null, "Bank account not found"),
    /** The maximum balance of a filter is lower than its minimum. */
    INVALID_BALANCE_RANGE(
            HttpStatus.BAD_REQUEST, "max_balance", "Maximum balance must not be lower than minimum balance");

    /** The HTTP status of the answer. */
    private final HttpStatus status;
    /** The request field the message refers to, or {@code null}. */
    private final @Nullable String field;
    /** The English text of the message. */
    private final String message;

    /**
     * Creates a constant.
     *
     * @param status the HTTP status of the answer
     * @param field the request field the message refers to, or {@code null}
     * @param message the English text of the message
     */
    BankAccountMessage(HttpStatus status, @Nullable String field, String message) {
        this.status = status;
        this.field = field;
        this.message = message;
    }

    /** The HTTP status of the answer. */
    @Override
    public HttpStatus status() {
        return status;
    }

    /** The request field the message refers to, or {@code null}. */
    @Override
    public @Nullable String field() {
        return field;
    }

    /** The English text of the message. */
    @Override
    public String message() {
        return message;
    }
}

package br.com.sergioluigi.personal_financial_control.commons.pagination;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessMessage;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

/** The business messages of pagination. */
public enum PaginationMessage implements BusinessMessage {

    /** The page number is not a non-negative integer. */
    INVALID_PAGE("page", "Page must be a non-negative integer");

    /** The request field the message refers to. */
    private final String field;
    /** The English text of the message. */
    private final String message;

    /**
     * Creates a constant.
     *
     * @param field the request field the message refers to
     * @param message the English text of the message
     */
    PaginationMessage(String field, String message) {
        this.field = field;
        this.message = message;
    }

    /** Always 400. */
    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }

    /** The request field the message refers to. */
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

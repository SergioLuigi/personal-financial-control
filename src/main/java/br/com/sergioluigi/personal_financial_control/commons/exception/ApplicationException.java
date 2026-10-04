package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.List;

/**
 * Base of the exceptions the API answers with a {@link ProblemDetail}: the status and the body are
 * carried by the exception itself.
 */
public class ApplicationException extends ErrorResponseException {

    /** Name of the {@code ProblemDetail} property that lists the field errors. */
    static final String ERRORS_PROPERTY = "errors";

    /**
     * Creates the exception.
     *
     * @param status the HTTP status of the response
     * @param body the {@code ProblemDetail} sent as the response body
     */
    public ApplicationException(
            HttpStatusCode status,
            ProblemDetail body
    ) {
        super(status, body, null);
    }

    /**
     * Builds the response body: the detail text and, when there are field errors, the {@code errors} list.
     *
     * @param status the HTTP status of the response
     * @param detail the summary of what went wrong
     * @param errors the field errors to list; none leaves the {@code errors} property out
     * @return the response body
     */
    static ProblemDetail problemDetail(HttpStatusCode status, String detail, List<FieldErrorDetail> errors) {
        var body = ProblemDetail.forStatusAndDetail(status, detail);

        if (!errors.isEmpty()) {
            body.setProperty(ERRORS_PROPERTY, errors);
        }

        return body;
    }
}

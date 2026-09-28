package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.List;

public class ApplicationException extends ErrorResponseException {

    static final String ERRORS_PROPERTY = "errors";

    public ApplicationException(
            HttpStatusCode status,
            ProblemDetail body
    ) {
        super(status, body, null);
    }

    static ProblemDetail problemDetail(HttpStatusCode status, String detail, List<FieldErrorDetail> errors) {
        var body = ProblemDetail.forStatusAndDetail(status, detail);

        if (!errors.isEmpty()) {
            body.setProperty(ERRORS_PROPERTY, errors);
        }

        return body;
    }
}

package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ApplicationException extends ErrorResponseException {
    public ApplicationException(
            HttpStatusCode status,
            ProblemDetail body
    ) {
        super(status, body, null);
    }
}

package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class AlreadyExistsException extends ApplicationException {

    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message));
    }
}

package br.com.sergioluigi.personal_financial_control.commons.exception;

import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final CurrentUser currentUser;

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException e) {

        var body = e.getBody();

        log.warn("Username: {} - error: {}", currentUser.findUsername().orElse("anonymous"), body.getDetail());

        return body;
    }
}

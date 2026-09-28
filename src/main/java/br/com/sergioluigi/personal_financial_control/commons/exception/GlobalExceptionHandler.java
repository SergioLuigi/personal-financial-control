package br.com.sergioluigi.personal_financial_control.commons.exception;

import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.databind.exc.MismatchedInputException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    static final String INVALID_CONTENT = "Invalid request content";

    static final String MALFORMED_BODY = "Malformed request body";

    static final String INVALID_AMOUNT = "Enter a valid amount";

    private final CurrentUser currentUser;

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        if (ex instanceof ApplicationException) {
            log.warn("Username: {} - error: {}",
                    currentUser.findUsername().orElse("anonymous"), ex.getBody().getDetail());
        }

        return super.handleErrorResponseException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(
                        error.getField(),
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value")))
                .sorted(Comparator.comparing(FieldErrorDetail::field).thenComparing(FieldErrorDetail::message))
                .toList();

        var body = ApplicationException.problemDetail(HttpStatus.BAD_REQUEST, INVALID_CONTENT, errors);

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var body = invalidAmountField(ex)
                .map(field -> ApplicationException.problemDetail(
                        HttpStatus.BAD_REQUEST, INVALID_CONTENT, List.of(new FieldErrorDetail(field, INVALID_AMOUNT))))
                .orElseGet(() -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, MALFORMED_BODY));

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // A value that cannot be read as an amount is a field error, not a malformed body.
    private static Optional<String> invalidAmountField(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof MismatchedInputException mismatch
                    && BigDecimal.class.equals(mismatch.getTargetType())
                    && !mismatch.getPath().isEmpty()) {
                return Optional.ofNullable(mismatch.getPath().getLast().getPropertyName());
            }
        }

        return Optional.empty();
    }
}

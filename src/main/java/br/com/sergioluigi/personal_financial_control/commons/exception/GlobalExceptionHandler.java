package br.com.sergioluigi.personal_financial_control.commons.exception;

import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
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
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Turns the exceptions of the API into {@link ProblemDetail} responses: business violations, Bean
 * Validation errors and unreadable request bodies.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** Detail of a response whose body lists field errors. */
    static final String INVALID_CONTENT = "Invalid request content";

    /** Detail of a response whose body could not be read at all. */
    static final String MALFORMED_BODY = "Malformed request body";

    /** Field message for an amount that cannot be read as a number. */
    static final String INVALID_AMOUNT = "Enter a valid amount";

    /** Field message for a value that cannot be read as its type. */
    static final String INVALID_VALUE = "Enter a valid value";

    /** Suffix of the field message for a field the request does not accept. */
    static final String CANNOT_BE_CHANGED = " cannot be changed";

    /** Names the user in the log line of an application error. */
    private final CurrentUser currentUser;

    /** Logs application errors with the user who caused them, then answers as usual. */
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

    /**
     * Answers a failed Bean Validation with a 400 that lists every invalid field, sorted by field and message.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(
                        toSnakeCase(error.getField()),
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value")))
                .sorted(Comparator.comparing(FieldErrorDetail::field).thenComparing(FieldErrorDetail::message))
                .toList();

        var body = ApplicationException.problemDetail(HttpStatus.BAD_REQUEST, INVALID_CONTENT, errors);

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Answers an unreadable body with a 400: a field error for an unchangeable field or a value of the wrong
     * type, or a generic "malformed body" when the cause is neither.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var body = unchangeableField(ex)
                .map(field -> new FieldErrorDetail(field, field + CANNOT_BE_CHANGED))
                .or(() -> invalidValue(ex))
                .map(error -> ApplicationException.problemDetail(
                        HttpStatus.BAD_REQUEST, INVALID_CONTENT, List.of(error)))
                .orElseGet(() -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, MALFORMED_BODY));

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * A value that cannot be read as its type is a field error, not a malformed body.
     *
     * @param ex the failure to inspect, along with its causes
     * @return the field error, or empty when no cause is a type mismatch on a named field
     */
    private static Optional<FieldErrorDetail> invalidValue(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof MismatchedInputException mismatch
                    && !(cause instanceof UnrecognizedPropertyException)
                    && !mismatch.getPath().isEmpty()
                    && mismatch.getPath().getLast().getPropertyName() != null) {
                var reference = mismatch.getPath().getLast();
                var field = reference.getPropertyName();

                return Optional.of(new FieldErrorDetail(field, invalidValueMessage(reference.from(), field, mismatch)));
            }
        }

        return Optional.empty();
    }

    /**
     * Chooses the message for a value of the wrong type: the one declared with {@link InvalidValueMessage} on
     * the request field, else the amount or generic message.
     *
     * @param from the object or class that declares the field
     * @param field the field name, in {@code snake_case}
     * @param mismatch the failure that reports the type mismatch
     * @return the message for the field
     */
    private static String invalidValueMessage(
            @Nullable Object from,
            String field,
            MismatchedInputException mismatch
    ) {
        var type = from instanceof Class<?> clazz ? clazz : from == null ? null : from.getClass();

        if (type != null && type.isRecord()) {
            for (var component : type.getRecordComponents()) {
                var annotation = component.getAnnotation(InvalidValueMessage.class);

                if (annotation != null && toSnakeCase(component.getName()).equals(field)) {
                    return annotation.value();
                }
            }
        }

        return BigDecimal.class.equals(mismatch.getTargetType()) ? INVALID_AMOUNT : INVALID_VALUE;
    }

    /**
     * Converts a {@code camelCase} name to {@code snake_case}.
     *
     * @param name the name to convert
     * @return the name in {@code snake_case}
     */
    private static String toSnakeCase(String name) {
        return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    /**
     * A field the request does not accept, such as the id in an update, rejects the whole request.
     *
     * @param ex the failure to inspect, along with its causes
     * @return the name of the rejected field, or empty when no cause is an unrecognized property
     */
    private static Optional<String> unchangeableField(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnrecognizedPropertyException unrecognized
                    && !unrecognized.getPath().isEmpty()) {
                return Optional.ofNullable(unrecognized.getPropertyName());
            }
        }

        return Optional.empty();
    }
}

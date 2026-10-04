package br.com.sergioluigi.personal_financial_control.commons.exception;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The message for a request field whose value cannot be read as its type — text where
 * a number is expected, for instance. Without it, {@link GlobalExceptionHandler}
 * answers with a generic message.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface InvalidValueMessage {

    /**
     * The message to answer with.
     *
     * @return the message
     */
    String value();
}

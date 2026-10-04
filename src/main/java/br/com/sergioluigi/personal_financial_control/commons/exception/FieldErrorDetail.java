package br.com.sergioluigi.personal_financial_control.commons.exception;

/**
 * One entry of the {@code errors} list of an error response.
 *
 * @param field the request field the error refers to, in {@code snake_case}
 * @param message the English text that describes the error
 */
public record FieldErrorDetail(String field, String message) {
}

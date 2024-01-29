package br.com.sergioluigi.personalfinancialcontrol.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum ExceptionsConstant {

    ACCOUNT_DOESNT_BELONG_TO_LOGGED_USER(INTERNAL_SERVER_ERROR, "Account doesn't belong to logged user"),

    AUTHENTICATION_ERROR(INTERNAL_SERVER_ERROR, "Authentication error"),

    ERROR_DURING_USER_UPDATE(INTERNAL_SERVER_ERROR, "Error during user update"),

    ASYNC_USER_DATA(INTERNAL_SERVER_ERROR, "Async user data"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"Category not found"),

    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT,"Category already exists"),

    CREDIT_CARD_NOT_FOUND(HttpStatus.NOT_FOUND,"Credit card not found"),

    CREDIT_CARD_NAME_ALREADY_IN_USE(HttpStatus.CONFLICT, "Credit card name already in use"),

    ACCOUNT_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Account name already in use"),

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found");

    private final HttpStatus status;

    private final String reason;


}

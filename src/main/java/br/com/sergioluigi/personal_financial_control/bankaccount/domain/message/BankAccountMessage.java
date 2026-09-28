package br.com.sergioluigi.personal_financial_control.bankaccount.domain.message;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum BankAccountMessage implements BusinessMessage {

    NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "name", "Bank account name already exists");

    private final HttpStatus status;

    private final @Nullable String field;

    private final String message;
}

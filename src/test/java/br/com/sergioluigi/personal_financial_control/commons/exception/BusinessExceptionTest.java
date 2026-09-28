package br.com.sergioluigi.personal_financial_control.commons.exception;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    private record Message(HttpStatus status, @Nullable String field, String message) implements BusinessMessage {
    }

    @Test
    void carriesTheStatusDetailAndFieldOfItsMessage() {
        var exception = new BusinessException(new Message(HttpStatus.CONFLICT, "name", "Name already exists"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getBody().getStatus()).isEqualTo(409);
        assertThat(exception.getBody().getDetail()).isEqualTo("Name already exists");
        assertThat(exception.getBody().getProperties())
                .containsEntry("errors", List.of(new FieldErrorDetail("name", "Name already exists")));
    }

    @Test
    void hasNoFieldErrorsWhenTheMessageHasNoField() {
        var exception = new BusinessException(new Message(HttpStatus.UNPROCESSABLE_CONTENT, null, "Rule violated"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(exception.getBody().getProperties()).isNull();
    }
}

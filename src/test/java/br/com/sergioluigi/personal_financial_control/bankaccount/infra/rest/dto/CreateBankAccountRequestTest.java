package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsOnlyAName() {
        assertThat(validator.validate(new CreateBankAccountRequest("savings", null, null))).isEmpty();
    }

    @Test
    void rejectsAMissingName() {
        assertThat(validator.validate(new CreateBankAccountRequest(null, null, null)))
                .extracting("message").containsExactly("Name is required");
    }

    @Test
    void rejectsANameThatIsBlankAfterTrimming() {
        assertThat(validator.validate(new CreateBankAccountRequest("   ", null, null)))
                .extracting("message").containsExactly("Name is required");
    }

    @Test
    void rejectsANameOver60Characters() {
        assertThat(validator.validate(new CreateBankAccountRequest("a".repeat(61), null, null)))
                .extracting("message").containsExactly("Name must be at most 60 characters");
    }

    @Test
    void trimsTheNameBeforeCountingCharacters() {
        assertThat(validator.validate(new CreateBankAccountRequest(" " + "a".repeat(60) + " ", null, null))).isEmpty();
    }

    @Test
    void rejectsADescriptionOver255Characters() {
        assertThat(validator.validate(new CreateBankAccountRequest("savings", "a".repeat(256), null)))
                .extracting("message").containsExactly("Description must be at most 255 characters");
    }

    @Test
    void rejectsABalanceWithMoreThanTwoDecimalPlaces() {
        assertThat(validator.validate(new CreateBankAccountRequest("savings", null, new BigDecimal("1.001"))))
                .extracting("message").containsExactly("Enter a valid amount");
    }
}

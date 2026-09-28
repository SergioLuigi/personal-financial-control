package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountRequestTest {

    private static jakarta.validation.ValidatorFactory factory;

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void acceptsANameOnly() {
        assertThat(messages(new CreateBankAccountRequest("Savings", null, null))).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void requiresAName(String name) {
        assertThat(messages(new CreateBankAccountRequest(name, null, null))).containsExactly("Name is required");
    }

    @Test
    void rejectsANameLongerThan60Characters() {
        assertThat(messages(new CreateBankAccountRequest("a".repeat(61), null, null)))
                .containsExactly("Name must be at most 60 characters");
    }

    @Test
    void checksTheNameLengthAfterTrimming() {
        var request = new CreateBankAccountRequest("  " + "a".repeat(60) + "  ", null, null);

        assertThat(messages(request)).isEmpty();
        assertThat(request.name()).hasSize(60);
    }

    @Test
    void rejectsADescriptionLongerThan255Characters() {
        assertThat(messages(new CreateBankAccountRequest("Savings", "a".repeat(256), null)))
                .containsExactly("Description must be at most 255 characters");
    }

    @Test
    void turnsABlankDescriptionIntoNull() {
        assertThat(new CreateBankAccountRequest("Savings", "   ", null).description()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.001", "100000000000.00", "-100000000000.00"})
    void rejectsAnInvalidAmount(String balance) {
        assertThat(messages(new CreateBankAccountRequest("Savings", null, new BigDecimal(balance))))
                .containsExactly("Enter a valid amount");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-150.00", "99999999999.99", "-99999999999.99"})
    void acceptsAValidAmount(String balance) {
        assertThat(messages(new CreateBankAccountRequest("Savings", null, new BigDecimal(balance)))).isEmpty();
    }

    @Test
    void mapsToTheDomainWithALowercaseName() {
        var newBankAccount = new CreateBankAccountRequest(" Savings ", " Emergency fund ", new BigDecimal("10.50")).toDomain();

        assertThat(newBankAccount.name()).isEqualTo("savings");
        assertThat(newBankAccount.description()).isEqualTo("Emergency fund");
        assertThat(newBankAccount.balance()).isEqualTo(new BigDecimal("10.50"));
    }

    private static Set<String> messages(CreateBankAccountRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}

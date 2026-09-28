package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest;

import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankAccountController.class)
@Import(CurrentUser.class)
class BankAccountControllerTest {

    private static final UUID ID = UUID.fromString("0b5f7a3e-6f0d-4c1e-9a53-2f4a8b1c9d10");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateBankAccountUseCase createBankAccount;

    @Test
    void returnsTheCreatedAccount() throws Exception {
        given(createBankAccount.execute(new NewBankAccount("Savings", "Emergency fund", new BigDecimal("1000.00"))))
                .willReturn(new BankAccount(ID, "alice", "savings", "Emergency fund", new BigDecimal("1000.00")));

        post("""
                {"name": " Savings ", "description": "Emergency fund", "balance": 1000.00}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.name").value("savings"))
                .andExpect(jsonPath("$.description").value("Emergency fund"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.owner").doesNotExist())
                .andExpect(jsonPath("$.created_by").doesNotExist());
    }

    @Test
    void returnsANullDescriptionWhenAbsent() throws Exception {
        given(createBankAccount.execute(any()))
                .willReturn(new BankAccount(ID, "alice", "savings", null, new BigDecimal("0.00")));

        post("""
                {"name": "Savings"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.balance").value(0.00));
    }

    @Test
    void reportsEveryInvalidFieldTogether() throws Exception {
        post("""
                {"name": "   ", "description": "%s"}
                """.formatted("a".repeat(256)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid request content"))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].message").value("Description must be at most 255 characters"))
                .andExpect(jsonPath("$.errors[1].field").value("name"))
                .andExpect(jsonPath("$.errors[1].message").value("Name is required"));

        verify(createBankAccount, never()).execute(any());
    }

    @Test
    void reportsANameThatIsTooLong() throws Exception {
        post("""
                {"name": "%s"}
                """.formatted("a".repeat(61)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name must be at most 60 characters"));
    }

    @Test
    void reportsABalanceThatIsNotANumber() throws Exception {
        post("""
                {"name": "Savings", "balance": "abc"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("balance"))
                .andExpect(jsonPath("$.errors[0].message").value("Enter a valid amount"));
    }

    @Test
    void reportsABalanceWithTooManyDecimalPlaces() throws Exception {
        post("""
                {"name": "Savings", "balance": 10.001}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("balance"))
                .andExpect(jsonPath("$.errors[0].message").value("Enter a valid amount"));
    }

    @Test
    void reportsAMalformedBody() throws Exception {
        post("{\"name\": ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Malformed request body"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void reportsANameAlreadyTaken() throws Exception {
        given(createBankAccount.execute(any())).willThrow(new BusinessException(NAME_ALREADY_EXISTS));

        post("""
                {"name": "Savings"}
                """)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Bank account name already exists"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Bank account name already exists"));
    }

    @Test
    void ignoresOwnerFieldsSentByTheClient() throws Exception {
        given(createBankAccount.execute(new NewBankAccount("Savings", null, null)))
                .willReturn(new BankAccount(ID, "alice", "savings", null, new BigDecimal("0.00")));

        post("""
                {"name": "Savings", "owner": "bob", "created_by": "bob", "createdBy": "bob"}
                """)
                .andExpect(status().isCreated());
    }

    private ResultActions post(String body) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post("/bank-accounts")
                        .with(user("alice"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body));
    }
}

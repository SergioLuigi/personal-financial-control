package br.com.sergioluigi.personal_financial_control.bankaccount.infra.rest;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.commons.exception.GlobalExceptionHandler;
import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.CreateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.DeleteBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.GetBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.PreviewBankAccountDeletionUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.ListBankAccountsUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.UpdateBankAccountUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The HTTP contract of the bank account endpoints, with the use cases mocked. */
@WebMvcTest(BankAccountController.class)
@Import(GlobalExceptionHandler.class)
class BankAccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CreateBankAccountUseCase createBankAccountUseCase;

    @MockitoBean
    ListBankAccountsUseCase listBankAccountsUseCase;

    @MockitoBean
    GetBankAccountUseCase getBankAccountUseCase;

    @MockitoBean
    UpdateBankAccountUseCase updateBankAccountUseCase;

    @MockitoBean
    DeleteBankAccountUseCase deleteBankAccountUseCase;

    @MockitoBean
    PreviewBankAccountDeletionUseCase previewBankAccountDeletionUseCase;

    @MockitoBean
    CurrentUser currentUser;

    @Test
    void createsAnAccountAndReturnsIt() throws Exception {
        var id = UUID.randomUUID();
        when(createBankAccountUseCase.execute(eq("alice"), any()))
                .thenReturn(new BankAccount(id, "alice", "savings", null, new BigDecimal("1000.00")));

        mockMvc.perform(post("/bank-accounts").with(user("alice")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": " Savings ", "balance": 1000.00 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("savings"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void reportsAnInvalidNameOnTheNameField() throws Exception {
        mockMvc.perform(post("/bank-accounts").with(user("alice")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "   " }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name is required"));
    }

    @Test
    void reportsAnInvalidBalanceOnTheBalanceField() throws Exception {
        mockMvc.perform(post("/bank-accounts").with(user("alice")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "savings", "balance": "abc" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("balance"))
                .andExpect(jsonPath("$.errors[0].message").value("Enter a valid amount"));
    }

    @Test
    void reportsANameAlreadyTakenAsAConflict() throws Exception {
        when(createBankAccountUseCase.execute(eq("alice"), any()))
                .thenThrow(new BusinessException(BankAccountMessage.NAME_ALREADY_EXISTS));

        mockMvc.perform(post("/bank-accounts").with(user("alice")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "savings" }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Bank account name already exists"));
    }
}

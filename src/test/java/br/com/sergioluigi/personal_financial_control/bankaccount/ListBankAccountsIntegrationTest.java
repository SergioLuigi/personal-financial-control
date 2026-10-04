package br.com.sergioluigi.personal_financial_control.bankaccount;

import br.com.sergioluigi.personal_financial_control.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.HashSet;

import static br.com.sergioluigi.personal_financial_control.support.BankAccountApi.createBankAccount;
import static br.com.sergioluigi.personal_financial_control.support.Users.newUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.jayway.jsonpath.JsonPath.read;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ListBankAccountsIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void ac01_ac02_ac03_pagesTenAtATime() throws Exception {
        var alice = newUser();
        for (var i = 0; i < 25; i++) {
            createBankAccount(mockMvc, alice, "account %02d".formatted(i));
        }

        list(alice, "")
                .andExpect(jsonPath("$.items.length()").value(10))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.total_items").value(25))
                .andExpect(jsonPath("$.total_pages").value(3));
        list(alice, "page=2")
                .andExpect(jsonPath("$.items.length()").value(5));
        list(alice, "page=3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.total_items").value(25));
    }

    @Test
    void ac10_pagingVisitsEveryAccountOnceInNameOrder() throws Exception {
        var alice = newUser();
        for (var i = 0; i < 25; i++) {
            createBankAccount(mockMvc, alice, "account %02d".formatted(i));
        }

        var names = new java.util.ArrayList<String>();
        var ids = new HashSet<String>();
        for (var page = 0; page < 3; page++) {
            var body = list(alice, "page=" + page).andReturn().getResponse().getContentAsString();
            names.addAll(read(body, "$.items[*].name"));
            ids.addAll(read(body, "$.items[*].id"));
        }

        assertThat(ids).hasSize(25);
        assertThat(names).isSorted();
    }

    @Test
    void ac04_aUserWithNoAccountsGetsAnEmptyPage() throws Exception {
        list(newUser(), "")
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.total_items").value(0))
                .andExpect(jsonPath("$.total_pages").value(1));
    }

    @Test
    void ac05_aUserSeesOnlyTheirOwnAccounts() throws Exception {
        var alice = newUser();
        var bob = newUser();
        createBankAccount(mockMvc, alice, "savings");
        createBankAccount(mockMvc, bob, "savings");
        createBankAccount(mockMvc, bob, "checking");

        list(alice, "").andExpect(jsonPath("$.total_items").value(1));
    }

    @Test
    void ac06_filtersByNameIgnoringCase() throws Exception {
        var alice = newUser();
        createBankAccount(mockMvc, alice, "savings");
        createBankAccount(mockMvc, alice, "SAVINGS POT");
        createBankAccount(mockMvc, alice, "checking");

        list(alice, "name=SAV")
                .andExpect(jsonPath("$.total_items").value(2))
                .andExpect(jsonPath("$.items[*].name", contains("savings", "savings pot")));
    }

    @Test
    void ac07_aBalanceRangeIsInclusive() throws Exception {
        var alice = newUser();
        createBankAccount(mockMvc, alice, "empty");
        createBankAccount(mockMvc, alice, "full", new BigDecimal("10.00"));

        list(alice, "min_balance=0&max_balance=0")
                .andExpect(jsonPath("$.total_items").value(1))
                .andExpect(jsonPath("$.items[0].name").value("empty"));
    }

    @Test
    void ac08_filtersCombineWithAnd() throws Exception {
        var alice = newUser();
        createBankAccount(mockMvc, alice, "savings", new BigDecimal("50.00"));
        createBankAccount(mockMvc, alice, "savings pot", new BigDecimal("150.00"));
        createBankAccount(mockMvc, alice, "checking", new BigDecimal("150.00"));

        list(alice, "name=sav&min_balance=100")
                .andExpect(jsonPath("$.total_items").value(1))
                .andExpect(jsonPath("$.items[0].name").value("savings pot"));
    }

    @Test
    void ac09_rejectsInvalidParameters() throws Exception {
        var alice = newUser();

        list(alice, "min_balance=10&max_balance=5")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Maximum balance must not be lower than minimum balance"));
    }

    @Test
    void readsAPageThatIsNotANonNegativeNumberAsTheFirstOne() throws Exception {
        var alice = newUser();

        list(alice, "page=-1").andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0));
        list(alice, "page=abc").andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void rejectsAnUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/bank-accounts")).andExpect(status().isUnauthorized());
    }

    private ResultActions list(String username, String query) throws Exception {
        return mockMvc.perform(get("/bank-accounts?" + query).with(user(username)));
    }
}

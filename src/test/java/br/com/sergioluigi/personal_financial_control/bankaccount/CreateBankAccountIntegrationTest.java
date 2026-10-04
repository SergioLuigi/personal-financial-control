package br.com.sergioluigi.personal_financial_control.bankaccount;

import br.com.sergioluigi.personal_financial_control.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CreateBankAccountIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcClient jdbcClient;

    @Test
    void ac01_createsAnAccountWithOnlyANameAndZeroBalance() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \"savings\" }")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(0.00));
    }

    @Test
    void ac02_rejectsANameAlreadyTaken() throws Exception {
        var alice = newUser();
        createBankAccount(alice, "{ \"name\": \"savings\" }");

        createBankAccount(alice, "{ \"name\": \"savings\" }")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Bank account name already exists"));

        assertThat(countBankAccounts(alice)).isEqualTo(1);
    }

    @Test
    void ac03_aNameCollidesIgnoringCaseAndSurroundingSpaces() throws Exception {
        var alice = newUser();
        createBankAccount(alice, "{ \"name\": \"Savings\" }");

        createBankAccount(alice, "{ \"name\": \"savings\" }").andExpect(status().isConflict());
        createBankAccount(alice, "{ \"name\": \" Savings \" }").andExpect(status().isConflict());
    }

    @Test
    void ac04_twoUsersCanOwnAnAccountWithTheSameName() throws Exception {
        var alice = newUser();
        var bob = newUser();
        createBankAccount(alice, "{ \"name\": \"Savings\" }");

        createBankAccount(bob, "{ \"name\": \"Savings\" }").andExpect(status().isCreated());
    }

    @Test
    void ac05_storesANegativeBalance() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \"savings\", \"balance\": -150.00 }")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(-150.00));
    }

    @Test
    void ac06_rejectsAnUnauthenticatedRequest() throws Exception {
        var before = countAllBankAccounts();

        mockMvc.perform(post("/bank-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"savings\" }"))
                .andExpect(status().isUnauthorized());

        assertThat(countAllBankAccounts()).isEqualTo(before);
    }

    @Test
    void ac07_rejectsADescriptionOf256Characters() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \"savings\", \"description\": \"" + "a".repeat(256) + "\" }")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].message").value("Description must be at most 255 characters"));

        assertThat(countBankAccounts(alice)).isZero();
    }

    @Test
    void ac08_anInitialBalanceIsTheAccountBalance() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \"savings\", \"balance\": 1000.00 }")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(1000.00));

        assertThat(readBalance(alice, "savings")).isEqualByComparingTo("1000.00");
    }

    @Test
    void ac09_aNameCollidesIgnoringAccents() throws Exception {
        var alice = newUser();
        createBankAccount(alice, "{ \"name\": \"Poupança\" }");

        createBankAccount(alice, "{ \"name\": \"poupanca\" }").andExpect(status().isConflict());
    }

    @Test
    void ac10_storesAndShowsTheNameInLowercase() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \" Savings \" }")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("savings"));

        assertThat(readNames(alice)).containsExactly("savings");
    }

    @Test
    void recordsTheOwnerInTheAuditFields() throws Exception {
        var alice = newUser();

        createBankAccount(alice, "{ \"name\": \"savings\" }");

        assertThat(readCreatedBy(alice)).containsExactly(alice);
    }

    private String newUser() {
        return "user-" + UUID.randomUUID();
    }

    private ResultActions createBankAccount(String username, String body) throws Exception {
        return mockMvc.perform(post("/bank-accounts").with(user(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private int countBankAccounts(String owner) {
        return jdbcClient.sql("select count(*) from bank_account where created_by = ?")
                .param(owner).query(Integer.class).single();
    }

    private int countAllBankAccounts() {
        return jdbcClient.sql("select count(*) from bank_account").query(Integer.class).single();
    }

    private java.math.BigDecimal readBalance(String owner, String name) {
        return jdbcClient.sql("select balance from bank_account where created_by = ? and name = ?")
                .param(owner).param(name).query(java.math.BigDecimal.class).single();
    }

    private java.util.List<String> readNames(String owner) {
        return jdbcClient.sql("select name from bank_account where created_by = ?")
                .param(owner).query(String.class).list();
    }

    private java.util.List<String> readCreatedBy(String owner) {
        return jdbcClient.sql("select created_by from bank_account where created_by = ? and updated_by = ?")
                .param(owner).param(owner).query(String.class).list();
    }
}

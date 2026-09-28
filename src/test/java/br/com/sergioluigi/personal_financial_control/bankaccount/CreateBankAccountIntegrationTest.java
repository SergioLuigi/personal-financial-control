package br.com.sergioluigi.personal_financial_control.bankaccount;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.TestcontainersConfiguration;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CreateBankAccountIntegrationTest {

    private static final RequestPostProcessor ALICE = user("alice");

    private static final RequestPostProcessor BOB = user("bob");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void cleanUp() {
        jdbcClient.sql("delete from bank_account").update();
    }

    @Test
    void ac01_createsAnAccountWithOnlyAName() throws Exception {
        create(ALICE, "{\"name\": \"Savings\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("savings"))
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.balance").value(0.00));

        assertThat(countOwnedBy("alice")).isEqualTo(1);
    }

    @Test
    void ac02_ac03_rejectsANameAlreadyTakenByTheSameUser() throws Exception {
        create(ALICE, "{\"name\": \"Savings\"}").andExpect(status().isCreated());

        for (var name : new String[]{"Savings", "savings", " Savings ", "SAVINGS"}) {
            create(ALICE, "{\"name\": \"%s\"}".formatted(name))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errors[0].field").value("name"))
                    .andExpect(jsonPath("$.errors[0].message").value("Bank account name already exists"));
        }

        assertThat(countOwnedBy("alice")).isEqualTo(1);
    }

    @Test
    void ac04_differentUsersMayUseTheSameName() throws Exception {
        create(ALICE, "{\"name\": \"Savings\"}").andExpect(status().isCreated());
        create(BOB, "{\"name\": \"Savings\"}").andExpect(status().isCreated());

        assertThat(countOwnedBy("alice")).isEqualTo(1);
        assertThat(countOwnedBy("bob")).isEqualTo(1);
    }

    @Test
    void ac05_storesANegativeBalance() throws Exception {
        var id = idOf(create(ALICE, "{\"name\": \"Savings\", \"balance\": -150.00}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(-150.00)));

        assertThat(row(id).get("balance")).isEqualTo(new BigDecimal("-150.00"));
    }

    @Test
    void ac06_rejectsUnauthenticatedRequests() throws Exception {
        mockMvc.perform(request("{\"name\": \"Savings\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(request("{\"name\": \"Savings\"}").with(httpBasic("user", "wrong")))
                .andExpect(status().isUnauthorized());

        assertThat(countAll()).isZero();
    }

    @Test
    void ac07_rejectsADescriptionOf256Characters() throws Exception {
        create(ALICE, "{\"name\": \"Savings\", \"description\": \"%s\"}".formatted("a".repeat(256)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].message").value("Description must be at most 255 characters"));

        assertThat(countAll()).isZero();
    }

    @Test
    void ac08_storesTheInitialBalanceWithoutAnyAccountingMonth() throws Exception {
        var id = idOf(create(ALICE, "{\"name\": \"Savings\", \"balance\": 1000.00}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(1000.00)));

        assertThat(row(id).get("balance")).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void ac09_comparesNamesIgnoringAccents() throws Exception {
        create(ALICE, "{\"name\": \"Poupança\"}").andExpect(status().isCreated());

        create(ALICE, "{\"name\": \"poupanca\"}").andExpect(status().isConflict());
    }

    @Test
    void ac10_storesTheNameTrimmedAndInLowercase() throws Exception {
        var id = idOf(create(ALICE, "{\"name\": \" Poupança \"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("poupança")));

        assertThat(row(id).get("name")).isEqualTo("poupança");
    }

    @Test
    void t07_fillsInTheAuditFields() throws Exception {
        var id = idOf(create(ALICE, "{\"name\": \"Savings\"}")
                .andExpect(jsonPath("$.created_by").doesNotExist())
                .andExpect(jsonPath("$.createdBy").doesNotExist()));

        var row = row(id);
        assertThat(row.get("created_by")).isEqualTo("alice");
        assertThat(row.get("updated_by")).isEqualTo("alice");
        assertThat(row.get("created_at")).isNotNull().isEqualTo(row.get("updated_at"));
        assertThat((LocalDateTime) row.get("created_at")).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    }

    @Test
    void t08_theUniqueKeyRejectsANameTheRuleDidNotSee() {
        bankAccountRepository.create(BankAccount.create("alice", new NewBankAccount("savings", null, null)));

        // Simulates a concurrent request that passed the rule check before the first insert.
        var duplicate = BankAccount.create("alice", new NewBankAccount("Savings", null, null));

        assertThatThrownBy(() -> bankAccountRepository.create(duplicate))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getBusinessMessage()).isEqualTo(NAME_ALREADY_EXISTS));
        assertThat(countOwnedBy("alice")).isEqualTo(1);
    }

    @Test
    void t11_theOwnerCannotBeChosenByTheClient() throws Exception {
        var id = idOf(create(ALICE, "{\"name\": \"Savings\", \"owner\": \"bob\", \"created_by\": \"bob\", \"createdBy\": \"bob\"}")
                .andExpect(status().isCreated()));

        assertThat(row(id).get("created_by")).isEqualTo("alice");
    }

    private ResultActions create(RequestPostProcessor user, String body) throws Exception {
        return mockMvc.perform(request(body).with(user));
    }

    private static MockHttpServletRequestBuilder request(String body) {
        return MockMvcRequestBuilders.post("/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    private UUID idOf(ResultActions result) throws Exception {
        var json = result.andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(json, "$.id"));
    }

    private Map<String, Object> row(UUID id) {
        return jdbcClient.sql("select * from bank_account where id = ?").param(id.toString()).query().singleRow();
    }

    private long countOwnedBy(String owner) {
        return jdbcClient.sql("select count(*) from bank_account where created_by = ?").param(owner).query(Long.class).single();
    }

    private long countAll() {
        return jdbcClient.sql("select count(*) from bank_account").query(Long.class).single();
    }
}

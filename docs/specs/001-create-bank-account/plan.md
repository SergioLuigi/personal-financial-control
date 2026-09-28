# Plan 001 — Create a bank account

**Status:** Implemented · **Last updated:** 2026-09-28
**Spec:** [spec.md](spec.md) · **Tasks:** [tasks.md](tasks.md)

How [spec.md](spec.md) is built, following the decisions of spec section 8.

## 1. Constraints

The plan follows the project rules; nothing here overrides them.

| Rule                                         | Consequence for this plan                                                     |
|----------------------------------------------|-------------------------------------------------------------------------------|
| `.claude/rules/architecture.md`, Modules     | New module `bankaccount`, `@ApplicationModule` + `@NullMarked`, depending only on `commons::*`. |
| `.claude/rules/architecture.md`, Layers      | `domain` ← `application` ← `infra`; file layout in section 2.                 |
| `.claude/rules/architecture.md`, Errors      | Rule violations are `BusinessException`s built from domain messages, rendered as `ProblemDetail`. |
| `.claude/rules/language.md`                  | Code, messages and changelogs in English.                                     |
| `.claude/rules/database.md`, `naming.md`     | Lowercase object names; UUIDs as `varchar(36)`; user names stored lowercase. |
| NFR-01                                       | Entity embeds `commons.audit.AuditEntity`; auditing fills it.                 |

## 2. Files

```
commons/audit/
└── AuditEntity.java                       created_by, created_at not updatable

commons/exception/
├── ApplicationException.java              existing — adds the ProblemDetail builder
├── FieldErrorDetail.java                  new — record (field, message)
├── BusinessMessage.java                   new — status, field, message
├── BusinessException.java                 new — the single business exception
└── GlobalExceptionHandler.java            filled in

bankaccount/
├── package-info.java                      @ApplicationModule, @NullMarked
├── domain/
│   ├── message/
│   │   └── BankAccountMessage.java        enum implementing BusinessMessage
│   ├── model/
│   │   ├── BankAccount.java               record: id, owner, name, description, balance
│   │   └── NewBankAccount.java            record: name, description, balance
│   ├── repository/
│   │   └── BankAccountRepository.java     port
│   └── rule/
│       └── BankAccountNameIsUniqueForOwnerRule.java
├── application/usecase/
│   ├── CreateBankAccountUseCase.java
│   └── impl/CreateBankAccountUseCaseImpl.java
└── infra/
    ├── rest/
    │   ├── BankAccountController.java
    │   └── dto/
    │       ├── CreateBankAccountRequest.java
    │       └── BankAccountResponse.java
    └── repository/
        ├── BankAccountJpaRepository.java
        ├── BankAccountRepositoryImpl.java
        └── entity/BankAccountJpaEntity.java

resources/
├── application.yaml                       environment-independent settings only
└── application-local.yaml                 new — compose MySQL (profile `local`)

resources/db/changelog/
├── db.changelog-master.yaml               includes both changes below
└── changes/
    ├── 001-create-bank-account-table.yaml
    └── 002-create-event-publication-table.yaml
```

No `repository/model/` or `spec/` package: UC-01 has no filter or dynamic
query.

## 3. Design

### 3.1 Domain

- `NewBankAccount(String name, @Nullable String description, @Nullable BigDecimal balance)`
  — what the user asked for. Its compact constructor trims the name and
  lowercases it with `Locale.ROOT` (REQ-07, `.claude/rules/naming.md`).
- `BankAccount(UUID id, String owner, String name, @Nullable String description, BigDecimal balance)`
  — `owner` is persisted as `created_by` (DEC-7)
  with a factory `BankAccount.create(String owner, NewBankAccount data)`:
  generates a random UUID and keeps `balance`, or sets `0.00`
  when absent (REQ-04, REQ-06). The domain model carries no audit fields:
  NFR-01 keeps them out of the API, and nothing in UC-01 reads them.
- `BankAccountRepository`:
  - `BankAccount create(BankAccount account)`
  - `boolean existsByOwnerAndName(String owner, String name)` — documented as
    comparing names ignoring case and accents (REQ-03).
- `BankAccountMessage.NAME_ALREADY_EXISTS` — 409, field `name`,
  `Bank account name already exists` (EF-01).
- `BankAccountNameIsUniqueForOwnerRule.check(String owner, String name)` —
  throws `new BusinessException(NAME_ALREADY_EXISTS)`.

### 3.2 Application

- `CreateBankAccountUseCase.execute(NewBankAccount data): BankAccount`.
- `CreateBankAccountUseCaseImpl` — package-private, `@Service`,
  `@Transactional`:
  1. `owner = currentUser.getUsername()` (`commons.security.CurrentUser`);
  2. `rule.check(owner, data.name())`;
  3. `return repository.create(BankAccount.create(owner, data))`.

### 3.3 REST

- `BankAccountController` — package-private, `@RequestMapping("/bank-accounts")`.
  `POST`, annotated `@ResponseStatus(HttpStatus.CREATED)`, takes
  `@Valid @RequestBody CreateBankAccountRequest`, calls the use case and
  returns `BankAccountResponse.from(account)`.
- `CreateBankAccountRequest` record:
  - compact constructor trims `name` and `description`, and turns a blank
    description into `null`, so that Bean Validation sees normalized values
    (REQ-07, T-02, T-04);
  - `name`: `@NotBlank(message = "Name is required")`,
    `@Size(max = 60, message = "Name must be at most 60 characters")`;
  - `description`: `@Size(max = 255, message = "Description must be at most 255 characters")`;
  - `balance`: `@Digits(integer = 11, fraction = 2, message = "Enter a valid amount")`;
  - `toDomain()` returns `NewBankAccount`.
- `BankAccountResponse(UUID id, String name, @Nullable String description, BigDecimal balance)`
  with `from(BankAccount)`.
- JSON naming: Jackson default; every field of UC-01 is a single word
  (DEC-2).

### 3.4 Errors (`commons.exception`)

- `FieldErrorDetail(String field, String message)`.
- `BusinessMessage` — `status()`, nullable `field()`, `message()`;
  implemented by each module's message enum.
- `BusinessException(BusinessMessage)` — the only business exception; its
  `ProblemDetail` has the message's status, the message as `detail`, and an
  `errors` entry when the message has a field.
- `GlobalExceptionHandler` (extends `ResponseEntityExceptionHandler`):
  - `handleMethodArgumentNotValid` → 400, `detail` `Invalid request content`,
    `errors` from the binding result, sorted by field.
  - `handleHttpMessageNotReadable` → 400. When the cause is a Jackson
    type mismatch on a `BigDecimal` property, returns that field with
    `Enter a valid amount` (T-03); otherwise `detail` `Malformed request body`
    and no `errors` (T-09).
  - `BusinessException`, as an `ApplicationException`, is already rendered by
    `ResponseEntityExceptionHandler`, as they extend `ErrorResponseException`.
  - Logs every handled `ApplicationException` at `WARN` with the username
    (or `anonymous`).
- 401 stays Spring Security's default response (DEP-07).

### 3.5 Persistence

- `AuditEntity` — `created_by` and `created_at` mapped with
  `@Column(updatable = false)`, so that no update statement can change the
  owner of any resource (DEC-7, REQ-10). `created_by` is **not** filled in by
  auditing (`@CreatedBy` would overwrite it with the current user, making
  `system` the owner of records created by automated processes); every entity
  sets it from its domain owner. Auditing fills in `created_at`, `updated_by`
  and `updated_at`.
- `BankAccountJpaEntity` — `@Table(name = "bank_account")`,
  `@EntityListeners(AuditingEntityListener.class)`, `@Embedded AuditEntity audit`,
  `id` as `UUID`, stored as `varchar(36)` through the global
  `hibernate.type.preferred_uuid_jdbc_type: VARCHAR` (`.claude/rules/database.md`).
  It implements `Persistable<UUID>` with `isNew()` true until first
  persisted, so that `save` with an id assigned by the domain issues a single
  `INSERT` instead of a `SELECT` + `INSERT`. Provides `from(BankAccount)` and
  `toDomain()`; the domain `owner` maps to `audit.createdBy`.
- `BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, UUID>`
  — package-private; `existsByAuditCreatedByAndName(String, String)`. The
  column collation `utf8mb4_0900_ai_ci` ignores case and accents, so the
  query stays a plain equality that uses the unique key.
- `BankAccountRepositoryImpl` — package-private adapter. `create` calls
  `saveAndFlush`; a `DataIntegrityViolationException` on
  `uk_bank_account_created_by_name` becomes `BusinessException(NAME_ALREADY_EXISTS)`
  (NFR-B, a race the rule cannot see).
- Liquibase:
  - `001-create-bank-account-table` — spec section 5, including the column
    collation and the unique key; `rollback` drops the table.
  - `002-create-event-publication-table` (DEC-5) — the table required by the Spring Modulith 2.1 JPA event
    publication entity, with columns taken from that entity's mapping.

### 3.6 Configuration and local environment

- `application.yaml` keeps only what every environment needs: application
  name, JPA and Liquibase settings, context path, actuator exposure, and the
  datasource read from `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` with **no
  default values** (DEC-6).
- `application-local.yaml` (profile `local`) sets the datasource to the MySQL
  service of `docker-compose.yaml`: `jdbc:mysql://localhost:3306/personal_financial_control`,
  user `root`, password `${DB_PASSWORD:root}` — the same default as the compose
  file.
- Running locally:

  ```bash
  docker compose up -d
  SPRING_PROFILES_ACTIVE=local bash gradlew bootRun
  ```

- Tests keep using Testcontainers (`@ServiceConnection`), which does not
  depend on either file's datasource values.
- No dependency is added and no volume is erased. If the existing `mysql_data`
  volume clashes with the changelog (DEP-09), the user decides what to do.

## 4. Tests

| Test class                                        | Kind                                  | Covers                               |
|---------------------------------------------------|---------------------------------------|--------------------------------------|
| `BankAccountTest`                                 | Unit                                  | REQ-04, REQ-06 (factory)             |
| `BankAccountNameIsUniqueForOwnerRuleTest`         | Unit, mocked port                     | EF-01                                |
| `BusinessExceptionTest`                           | Unit                                  | Status, detail and field of a message |
| `CreateBankAccountUseCaseImplTest`                | Unit, mocked port and `CurrentUser`   | REQ-01, order rule → save            |
| `CreateBankAccountRequestTest`                    | Unit, Bean Validation                 | REQ-07, T-01, T-02, T-04, AC-07      |
| `BankAccountControllerTest`                       | `@WebMvcTest`, mocked use case        | Contract of spec 4.2 and 4.3, T-03, T-06, T-09, AC-06 |
| `CreateBankAccountIntegrationTest`                | `@SpringBootTest` + Testcontainers    | AC-01 to AC-10, T-07, T-08, T-11     |
| `ModularityTests`                                 | Existing                              | T-10                                 |

Integration tests authenticate with `SecurityMockMvcRequestPostProcessors.user("alice")`
and `user("bob")`, and read the table directly to check rows and audit
fields.

## 5. Risks

- **Docker not reachable from WSL** (DEP-10): integration tests and `bootRun`
  both need it. Enable Docker Desktop's WSL integration, or install Docker
  Engine inside WSL.
- **Collation in Liquibase**: the column type is written as
  `varchar(60) COLLATE utf8mb4_0900_ai_ci`; if Liquibase rewrites it, fall
  back to a `sql` change for that column. AC-09 detects either failure.
- **Event publication schema**: if the Spring Modulith entity mapping differs
  from the changelog, the application fails at start (`ddl-auto: validate`),
  which `contextLoads` detects.

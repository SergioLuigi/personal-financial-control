# Spec 001 — Create a bank account

**Status:** Implemented · **Last updated:** 2026-09-28
**Source:** [UC-01 — Create a bank account](../../use-cases/01-bank-accounts.md#uc-01--create-a-bank-account),
[NFR-01 — Audit trail](../../README.md#nfr-01--audit-trail), [Glossary](../../glossary.md)
**Plan:** [plan.md](plan.md) · **Tasks:** [tasks.md](tasks.md)

This spec states **what** the first delivery of UC-01 must do and how its
result is verified. It does not restate UC-01: the use case stays the
authoritative source of business behaviour, and every requirement below
points back to it. **How** it is built is in [plan.md](plan.md).

## 1. Goal

Expose a REST endpoint that lets an authenticated user create a bank account,
persisted in MySQL, enforcing every rule of UC-01 that does not depend on a
part of the system not yet built.

## 2. Scope

### In scope

| UC-01 item                | Delivered as                                                              |
|---------------------------|---------------------------------------------------------------------------|
| PRE-01                    | The endpoint requires authentication (existing HTTP Basic setup).         |
| POST-01                   | The account is persisted in MySQL and owned by the requesting user.      |
| POST-02 (server side)     | On any failure nothing is persisted.                                      |
| Data table                | Request contract, section 4.1.                                            |
| FR-01 to FR-05, FR-07     | Sections 3 and 4.                                                         |
| Main flow, steps 3–6      | The request (steps 3–4), validation (5) and the created response (6).    |
| EF-01 to EF-04            | Error responses, section 4.3, with the exact UC-01 messages.             |
| AC-01 to AC-10            | Automated tests, section 6.                                               |
| NFR-01                    | Audit fields filled in on creation.                                       |

### Out of scope — blocked by dependencies

See section 7 for each dependency.

| UC-01 item                                  | Why it is not delivered now                                        |
|---------------------------------------------|---------------------------------------------------------------------|
| FR-06 — entry points (UC-02, UC-38 AF-01)   | Navigation between screens; there is no UI (DEP-02).               |
| Main flow, steps 1–2 — empty form           | UI concern (DEP-02).                                                 |
| POST-02 — "the user keeps the data entered" | UI concern. The API supports it by returning per-field errors.     |
| EF messages "next to the field"             | UI placement. The API returns each message tied to its field.      |
| Step 6 — "Bank account <name> created"      | UI text. The API returns `201 Created` with the account.           |

## 3. Requirements

Each requirement is testable and traces to UC-01 (or NFR-01).

### Functional

- **REQ-01** (FR-01, POST-01) — A valid request persists one bank account whose
  owner is the authenticated user's username, stored as `created_by`
  (Glossary, "Owner").
- **REQ-02** (FR-02) — A bank account has an `id`, a `name`, an optional
  `description` and a `balance`.
- **REQ-03** (FR-03) — `name` is unique per owner. Two names collide when they
  are equal after trimming surrounding whitespace, **ignoring case and
  accents**: `"savings"` and `" Savings "` collide with `"Savings"`, and
  `"poupanca"` collides with `"Poupança"`. Names of different owners never
  collide.
- **REQ-04** (FR-04) — When `balance` is absent or `null`, the balance
  is `0.00`.
- **REQ-05** (FR-05) — The balance may be positive, zero or negative.
- **REQ-06** (FR-07) — The initial balance is stored as the account's balance
  as it is. Creating an account creates, reads or requires no accounting month.
- **REQ-07** (FR-03) — `name` is stored trimmed and in lowercase, accents
  kept (`.claude/rules/naming.md`). `description` is stored trimmed, and a
  description that is empty after trimming is stored as absent.
- **REQ-08** (EF-01 to EF-04) — Invalid requests are rejected with the UC-01
  message for each offending field, and nothing is persisted. All field errors
  of one request are reported together. Uniqueness (EF-01) is checked only
  once every field is valid.
- **REQ-09** (PRE-01, AC-06) — An unauthenticated request is rejected and
  persists nothing.
- **REQ-10** (NFR-01) — The owner cannot be chosen or changed: it is always
  the authenticated user, any owner or audit field sent in the request is
  ignored, and `created_by` is never updated once written.

### Non-functional

- **NFR-A** (NFR-01) — On creation the account stores `created_by` (the owner)
  and `updated_by` = the requesting user's username, and `created_at` = `updated_at` = the
  creation instant, in UTC. None of them is accepted from the request or
  returned in the response.
- **NFR-B** — REQ-03 holds under concurrency: two simultaneous requests with
  colliding names for the same owner produce exactly one account; the other
  request gets EF-01.
- **NFR-C** — The data lives in MySQL 8.4, and its schema is created by
  Liquibase. `application.yaml` holds only what every environment needs, with
  no environment-specific values; running locally against the MySQL service of
  `docker-compose.yaml` uses a separate `local` profile.
- **NFR-D** — The module follows `.claude/rules/architecture.md`, and
  `ModularityTests` keeps passing.

## 4. API contract

### 4.1 Request

`POST /personal-financial-control/bank-accounts` — authenticated,
`Content-Type: application/json`.

| Field             | JSON type | Required | Rule (applied after trimming)                   | Error |
|-------------------|-----------|----------|-------------------------------------------------|-------|
| `name`            | string    | Yes      | 1–60 characters; unique per owner (REQ-03)      | EF-02, EF-01 |
| `description`     | string    | No       | 0–255 characters                                | EF-04 |
| `balance`         | number    | No       | Initial balance; defaults to `0.00`; at most 2 decimal places; −99,999,999,999.99 to 99,999,999,999.99 (`decimal(13,2)`) | EF-03 |

Unknown fields are ignored.

```json
{
  "name": "Savings",
  "description": "Emergency fund",
  "balance": 1000.00
}
```

### 4.2 Success

`201 Created`, with the account as body.

```json
{
  "id": "0b5f7a3e-6f0d-4c1e-9a53-2f4a8b1c9d10",
  "name": "savings",
  "description": "Emergency fund",
  "balance": 1000.00
}
```

`name` is returned as stored, in lowercase. `description` is `null` when absent. `balance` is always a number with two
decimal places.

### 4.3 Errors

Every error body is an RFC 9457 `ProblemDetail` (`type` is omitted, which
means `about:blank`). Field errors are listed in an
`errors` extension, one entry per offending field, so a client can show each
message next to its field.

| Case                               | Status | `errors[].field`  | `errors[].message`                                  |
|------------------------------------|--------|-------------------|-----------------------------------------------------|
| EF-02 name missing or blank        | 400    | `name`            | `Name is required`                                  |
| EF-02 name too long                | 400    | `name`            | `Name must be at most 60 characters`                |
| EF-03 balance not a valid amount   | 400    | `balance`         | `Enter a valid amount`                              |
| EF-04 description too long         | 400    | `description`     | `Description must be at most 255 characters`        |
| Body missing or not valid JSON     | 400    | —                 | — (`detail`: `Malformed request body`)              |
| EF-01 name already taken           | 409    | `name`            | `Bank account name already exists`                  |
| Not authenticated (PRE-01)         | 401    | —                 | —                                                    |

EF-03 covers a value that is not a number (such as `"abc"`), a value with
more than two decimal places, and a value outside the range above.

Example (EF-02 and EF-04 together):

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content",
  "instance": "/personal-financial-control/bank-accounts",
  "errors": [
    { "field": "description", "message": "Description must be at most 255 characters" },
    { "field": "name", "message": "Name is required" }
  ]
}
```

Example (EF-01):

```json
{
  "title": "Conflict",
  "status": 409,
  "detail": "Bank account name already exists",
  "instance": "/personal-financial-control/bank-accounts",
  "errors": [
    { "field": "name", "message": "Bank account name already exists" }
  ]
}
```

## 5. Data

Table `bank_account`, created by Liquibase:

| Column        | Type            | Null | Notes                                                        |
|---------------|-----------------|------|--------------------------------------------------------------|
| `id`          | `varchar(36)`   | No   | Primary key; UUID (`.claude/rules/database.md`)              |
| `name`        | `varchar(60)`   | No   | Trimmed, lowercase; collation `utf8mb4_0900_ai_ci` (ignores case and accents) |
| `description` | `varchar(255)`  | Yes  | Trimmed; `NULL` when absent                                  |
| `balance`     | `decimal(13,2)` | No   |                                                              |
| `created_by`  | `varchar(255)`  | No   | NFR-01; **the owner**; never updated                         |
| `created_at`  | `datetime(6)`   | No   | NFR-01, UTC                                                  |
| `updated_by`  | `varchar(255)`  | No   | NFR-01                                                       |
| `updated_at`  | `datetime(6)`   | No   | NFR-01, UTC                                                  |

Unique key `uk_bank_account_created_by_name (created_by, name)` enforces
REQ-03 and NFR-B. It also serves as the index for every query scoped to the
owner.

`created_by` is the owner (DEC-7); `updated_by` only records who changed the
record last and never grants ownership. The collation is written explicitly
so that REQ-03 does not depend on the server's default.

## 6. Acceptance tests

Each UC-01 acceptance criterion becomes at least one automated test. "API"
means an HTTP request against the running application backed by MySQL
(Testcontainers).

| ID     | Given / When                                                   | Then                                                                                   |
|--------|----------------------------------------------------------------|----------------------------------------------------------------------------------------|
| AC-01  | User `alice` posts `{"name":"Savings"}`                        | 201; `name` `savings`; `balance` `0.00`; `description` `null`; one row owned by `alice` |
| AC-02  | `alice` owns "Savings" and posts "Savings" again               | 409 with the EF-01 message on `name`; `alice` still owns one account                  |
| AC-03  | `alice` owns "Savings" and posts "savings", then " Savings "   | 409 both times                                                                         |
| AC-04  | `alice` owns "Savings"; `bob` posts "Savings"                  | 201                                                                                    |
| AC-05  | `alice` posts `balance` `-150.00`                              | 201; stored and returned balance `-150.00`                                             |
| AC-06  | A request without credentials, or with wrong credentials       | 401; no row written                                                                    |
| AC-07  | Description of 256 characters                                  | 400 with the EF-04 message on `description`; no row written                            |
| AC-08  | `balance` `1000.00`, with no accounting month existing         | 201; balance `1000.00`                                                                 |
| AC-10  | `alice` posts `" Savings "`                                    | 201; stored and returned `name` `savings`                                              |
| T-01   | Name `""`, `"   "`, or missing                                 | 400, `Name is required`                                                                |
| T-02   | Name of 61 characters; name of 60 characters + surrounding spaces | 400 `Name must be at most 60 characters`; 201 (stored trimmed, 60 characters)      |
| T-03   | `balance` `"abc"`, `10.001`, `100000000000.00`                 | 400, `Enter a valid amount` on `balance`                                              |
| T-04   | Description `"   "`                                            | 201; `description` `null`                                                              |
| AC-09  | "Poupança" exists; post "poupanca"                             | 409 (accents and case ignored)                                                         |
| T-06   | Invalid name and too-long description together                 | 400 with both field errors                                                             |
| T-07   | Any created account                                            | `created_by` = `updated_by` = owner; `created_at` = `updated_at`, set; not in response |
| T-08   | Two concurrent requests creating "Savings" for `alice`         | One 201 and one 409; one row                                                           |
| T-09   | Body is not valid JSON                                         | 400, `Malformed request body`                                                          |
| T-10   | Modular structure                                              | `ModularityTests` passes                                                               |
| T-11   | `alice` posts a body that also carries `created_by: "bob"` and `owner: "bob"` | 201; the account belongs to `alice`                                        |

## 7. Dependencies

Found while mapping UC-01 onto the current code base. **Blocking** means a
UC-01 item cannot be delivered now; **prerequisite** means it must be built
inside this delivery before the use case works.

| ID     | Dependency                                | Kind         | Impact and handling                                                                                                                                                                                                                                                   |
|--------|-------------------------------------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DEP-01 | User accounts (sign-up, sign-in)          | Out of scope | Declared out of scope by the docs. PRE-01 is met by the existing HTTP Basic setup with one in-memory user (`user` / `password`). The owner is stored as the username in `created_by`; if users later get a separate id, that column needs a migration.                     |
| DEP-02 | Front end                                 | Blocking     | None exists. FR-06, steps 1–2, POST-02 on the client and message placement wait for it. The API contract (per-field errors) is designed so that the front end can meet them.                                                                                          |
| DEP-03 | UC-02 — List bank accounts                | Blocking     | FR-06's main entry point. Not needed by the endpoint. Tests check "account count unchanged" (AC-02) in the database, not through the API.                                                                                                                            |
| DEP-04 | UC-03 — View bank account details         | None now     | The response carries the account's `id`, from which a client reaches UC-03 once it exists. No `Location` header is sent: handlers return DTOs, not `ResponseEntity` (`.claude/rules/architecture.md`).                                                           |
| DEP-05 | UC-38 — Home summary (AF-01)              | Blocking     | FR-06's second entry point.                                                                                                                                                                                                                                          |
| DEP-06 | Accounting months, incomes, Pix, bills    | None now     | FR-07 only requires that no month is touched. Later modules will move the stored balance (Glossary, "Balance"); the `balance` column is the stored balance they will update.                                                                                           |
| DEP-07 | `commons.exception` infrastructure        | Prerequisite | `commons.exception` has only `ApplicationException` and an empty `GlobalExceptionHandler`. Needed: a business exception with messages kept in each domain, the `errors` extension, and the mapping of Bean Validation and unreadable-body errors. The 401 response stays Spring Security's default.      |
| DEP-08 | Spring Modulith event publication table   | Prerequisite | `spring-modulith-starter-jpa` maps a JPA entity for `event_publication`; with `ddl-auto: validate` and an empty Liquibase changelog, the application is expected not to start. UC-01 publishes no event.                                                            |
| DEP-09 | Local MySQL through `docker-compose.yaml` | Prerequisite | The service exists and is started by hand; the application reaches it through the `local` profile (DEC-6). The Docker volume `mysql_data` may hold tables from earlier runs that clash with the new changelog; if so, the user decides how to clean it up.         |
| DEP-10 | Docker for the tests                      | Prerequisite | Integration tests use Testcontainers, which reaches Docker Desktop. The `docker` CLI is not on the WSL path (`docker.exe` is), which does not affect the tests.                                                                                                     |

## 8. Decisions

| ID    | Decision                                                                                   | Status   |
|-------|--------------------------------------------------------------------------------------------|----------|
| DEC-1 | Path `/bank-accounts` (plural resource).                                                   | Approved |
| DEC-2 | The request field is `balance`, not `initial_balance`; absent means `0.00`. No request or response field of UC-01 has more than one word, so no JSON naming strategy is configured now (Jackson default). | Approved |
| DEC-3 | Name comparison ignores case and accents (`utf8mb4_0900_ai_ci`). UC-01 FR-03 updated to match. | Approved |
| DEC-4 | A blank description is stored as `NULL` (REQ-07); the description is optional.            | Approved |
| DEC-5 | DEP-08: create the `event_publication` table with Liquibase and keep `spring-modulith-starter-jpa` for future events. | Approved |
| DEC-6 | `application.yaml` keeps only environment-independent settings; a `local` profile (`application-local.yaml`) points to the compose MySQL. The compose file is started by hand; no extra dependency, and nothing is erased. | Approved |
| DEC-7 | `created_by` is the owner and is never updated; `updated_by` is never the owner. No resource can change owner. Recorded in NFR-01 and the Glossary. | Approved |

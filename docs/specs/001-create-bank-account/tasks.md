# Tasks 001 — Create a bank account

**Status:** Done · **Last updated:** 2026-09-28
**Spec:** [spec.md](spec.md) · **Plan:** [plan.md](plan.md)

Ordered. `[P]` marks tasks that can run in parallel with the previous one.
Each task ends with its tests passing; tests are written before or with the
code they cover.

## Phase 0 — Environment

- [x] **T0.1** Make `gradlew` executable. Confirm Testcontainers reaches
  Docker. *(DEP-10)*
- [x] **T0.2** Reduce `application.yaml` to environment-independent settings;
  add `application-local.yaml`. *(DEC-6)*
- [x] **T0.3** Changelog `002-create-event-publication-table`. *(DEC-5, DEP-08)*
- [x] **T0.4** `AuditEntity`: `created_by` and `created_at` not updatable.
  *(DEC-7)*

## Phase 1 — Shared error handling (`commons.exception`)

- [x] **T1.1** `FieldErrorDetail`, `BusinessMessage` and `BusinessException`;
  `BankAccountMessage` in the domain. *(DEP-07)*
- [x] **T1.2** `GlobalExceptionHandler`: validation errors, unreadable body,
  type mismatch on amounts, `WARN` log. *(spec 4.3)*

## Phase 2 — Domain

- [x] **T2.1** `NewBankAccount`, `BankAccount` + factory; `BankAccountTest`.
  *(REQ-02, REQ-04, REQ-06)*
- [x] **T2.2** [P] `BankAccountRepository` port.
- [x] **T2.3** `BankAccountNameIsUniqueForOwnerRule` + test. *(REQ-03, EF-01)*
- [x] **T2.4** `package-info.java` for the module; `ModularityTests` passes.

## Phase 3 — Application

- [x] **T3.1** `CreateBankAccountUseCase` + `Impl` + unit test. *(REQ-01)*

## Phase 4 — Persistence

- [x] **T4.1** Changelog `001-create-bank-account-table`. *(spec 5)*
- [x] **T4.2** `BankAccountJpaEntity`, `BankAccountJpaRepository`,
  `BankAccountRepositoryImpl` with the unique-key translation. *(NFR-A, NFR-B)*

## Phase 5 — REST

- [x] **T5.1** `CreateBankAccountRequest` + `CreateBankAccountRequestTest`.
  *(REQ-07, REQ-08)*
- [x] **T5.2** `BankAccountResponse`, `BankAccountController` +
  `BankAccountControllerTest`. *(spec 4)*

## Phase 6 — Acceptance

- [x] **T6.1** `CreateBankAccountIntegrationTest`: AC-01 to AC-10, T-07,
  T-08, T-11.
- [x] **T6.2** Full test suite green; `docker compose up -d`, run the app with
  the `local` profile and create an account with `curl` as user `user`.
- [x] **T6.3** Mark spec, plan and tasks as `Implemented`; record in
  spec section 2 anything delivered differently.

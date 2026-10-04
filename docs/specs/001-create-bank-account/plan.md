# Plan — 001 Create a bank account

1. Changelog `001-create-bank-account-table`: table `bank_account`, `pk_`,
   `uk_bank_account_created_by_name_deleted_id`, accent- and case-insensitive name.
2. Domain: `NewBankAccount`, `BankAccount`, `BankAccountRepository`,
   `BankAccountMessage`, `BankAccountNameIsUniqueForOwnerRule`.
3. Application: `CreateBankAccountUseCase` and its implementation.
4. Infra: JPA entity, Spring Data repository, adapter, request/response DTOs,
   controller.
5. Tests: unit (model, rule, use case, request), `@WebMvcTest` of the
   controller, integration test with one method per acceptance criterion.

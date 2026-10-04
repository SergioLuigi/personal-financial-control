# Plan — 002 List bank accounts

1. Domain: `BankAccountFilter` in `repository/model`,
   `BankAccountBalanceRangeIsValidRule`, `INVALID_BALANCE_RANGE` message; the
   repository interface gets a paged search.
2. Application: `ListBankAccountsUseCase` and its implementation.
3. Infra: one `Specification` record per criterion, repository adapter
   method, `GET /bank-accounts` in the controller, `commons.pagination`
   (`PageResponse`, `PageParam`) for the page.
4. Tests: rule unit test, integration test with one method per acceptance
   criterion.

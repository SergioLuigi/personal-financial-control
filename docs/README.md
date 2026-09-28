# Personal financial control — Documentation

**Status:** Draft · **Last updated:** 2026-09-28

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Documents

- [Glossary](glossary.md) — terms used with a single meaning across every document.
- [Use case diagrams](use-case-diagrams.md) — one flow diagram per use case, plus navigation and state diagrams.

## Non-functional requirements

Requirements that apply to every use case and are not repeated in each one.

### NFR-01 — Audit trail

- Every persisted record — bank accounts, credit cards, categories, expenses,
  charges, incomes, receipts, refunds, bill payments, accounting months and
  closings — stores its audit fields (Glossary, "Audit fields"):
  `created_by`, `created_at`, `updated_by` and `updated_at`.
- `created_by` and `updated_by` hold the username of the authenticated user
  who performed the operation. Records written by the automated processes
  (UC-41 to UC-43) use the author `system`.
- `created_at` and `updated_at` are timestamps stored in UTC.
- `created_by` and `created_at` are set once, when the record is created, and
  never change. `updated_by` and `updated_at` are set on creation and
  refreshed on every change, including a logical removal.
- The system fills in the audit fields automatically. They are never taken
  from the user's input, and they are not shown to the user or returned by
  the API.
- The audit fields do not replace the record's owner: the owner defines who
  may see the record; the audit fields record who created and last changed
  it, and when.

## Use cases by domain

### [Bank accounts](use-cases/01-bank-accounts.md) — UC-01 to UC-05

- [UC-01 — Create a bank account](use-cases/01-bank-accounts.md#uc-01--create-a-bank-account)
- [UC-02 — List bank accounts](use-cases/01-bank-accounts.md#uc-02--list-bank-accounts)
- [UC-03 — View bank account details](use-cases/01-bank-accounts.md#uc-03--view-bank-account-details)
- [UC-04 — Update a bank account](use-cases/01-bank-accounts.md#uc-04--update-a-bank-account)
- [UC-05 — Delete a bank account](use-cases/01-bank-accounts.md#uc-05--delete-a-bank-account)

### [Credit cards](use-cases/02-credit-cards.md) — UC-06 to UC-10

- [UC-06 — Create a credit card](use-cases/02-credit-cards.md#uc-06--create-a-credit-card)
- [UC-07 — View credit card details](use-cases/02-credit-cards.md#uc-07--view-credit-card-details)
- [UC-08 — Update a credit card](use-cases/02-credit-cards.md#uc-08--update-a-credit-card)
- [UC-09 — List credit cards](use-cases/02-credit-cards.md#uc-09--list-credit-cards)
- [UC-10 — Delete a credit card](use-cases/02-credit-cards.md#uc-10--delete-a-credit-card)

### [Categories](use-cases/03-categories.md) — UC-11 to UC-14

- [UC-11 — Create a category](use-cases/03-categories.md#uc-11--create-a-category)
- [UC-12 — List categories](use-cases/03-categories.md#uc-12--list-categories)
- [UC-13 — Update a category](use-cases/03-categories.md#uc-13--update-a-category)
- [UC-14 — Delete a category](use-cases/03-categories.md#uc-14--delete-a-category)

### [Expenses](use-cases/04-expenses.md) — UC-15 to UC-23

- [UC-15 — Register a credit card purchase](use-cases/04-expenses.md#uc-15--register-a-credit-card-purchase)
- [UC-16 — Register a Pix expense](use-cases/04-expenses.md#uc-16--register-a-pix-expense)
- [UC-17 — Schedule a Pix](use-cases/04-expenses.md#uc-17--schedule-a-pix)
- [UC-18 — View expense details](use-cases/04-expenses.md#uc-18--view-expense-details)
- [UC-19 — Update an expense](use-cases/04-expenses.md#uc-19--update-an-expense)
- [UC-20 — List expenses](use-cases/04-expenses.md#uc-20--list-expenses)
- [UC-21 — Remove an expense](use-cases/04-expenses.md#uc-21--remove-an-expense)
- [UC-22 — Stop a recurring expense or income](use-cases/04-expenses.md#uc-22--stop-a-recurring-expense-or-income)
- [UC-23 — Resume a recurring expense or income](use-cases/04-expenses.md#uc-23--resume-a-recurring-expense-or-income)

### [Incomes](use-cases/05-incomes.md) — UC-24 to UC-28

- [UC-24 — Register an income](use-cases/05-incomes.md#uc-24--register-an-income)
- [UC-25 — View income details](use-cases/05-incomes.md#uc-25--view-income-details)
- [UC-26 — Update an income](use-cases/05-incomes.md#uc-26--update-an-income)
- [UC-27 — List incomes](use-cases/05-incomes.md#uc-27--list-incomes)
- [UC-28 — Remove an income](use-cases/05-incomes.md#uc-28--remove-an-income)

### [Credit card bills](use-cases/06-credit-card-bills.md) — UC-29 to UC-32

- [UC-29 — View a credit card bill](use-cases/06-credit-card-bills.md#uc-29--view-a-credit-card-bill)
- [UC-30 — Pay a credit card bill](use-cases/06-credit-card-bills.md#uc-30--pay-a-credit-card-bill)
- [UC-31 — Remove a bill payment](use-cases/06-credit-card-bills.md#uc-31--remove-a-bill-payment)
- [UC-32 — Register a credit card refund](use-cases/06-credit-card-bills.md#uc-32--register-a-credit-card-refund)

### [Accounting months](use-cases/07-accounting-months.md) — UC-33 to UC-37

- [UC-33 — Create an accounting month](use-cases/07-accounting-months.md#uc-33--create-an-accounting-month)
- [UC-34 — Delete an accounting month](use-cases/07-accounting-months.md#uc-34--delete-an-accounting-month)
- [UC-35 — Set the accounting month closing day](use-cases/07-accounting-months.md#uc-35--set-the-accounting-month-closing-day)
- [UC-36 — Consolidate an accounting month](use-cases/07-accounting-months.md#uc-36--consolidate-an-accounting-month)
- [UC-37 — View an accounting month](use-cases/07-accounting-months.md#uc-37--view-an-accounting-month)

### [Overview and tools](use-cases/08-overview-and-tools.md) — UC-38 to UC-40

- [UC-38 — View the home summary](use-cases/08-overview-and-tools.md#uc-38--view-the-home-summary)
- [UC-39 — Import a credit card statement](use-cases/08-overview-and-tools.md#uc-39--import-a-credit-card-statement)
- [UC-40 — Export data](use-cases/08-overview-and-tools.md#uc-40--export-data)

### [Automated processes](use-cases/09-automated-processes.md) — UC-41 to UC-43

- [UC-41 — Record recurring charges and receipts](use-cases/09-automated-processes.md#uc-41--record-recurring-charges-and-receipts)
- [UC-42 — Carry out scheduled Pix](use-cases/09-automated-processes.md#uc-42--carry-out-scheduled-pix)
- [UC-43 — Process bill due dates](use-cases/09-automated-processes.md#uc-43--process-bill-due-dates)

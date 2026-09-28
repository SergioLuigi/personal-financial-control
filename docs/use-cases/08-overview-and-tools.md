# Overview and tools — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-38 to UC-40 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-38 — View the home summary](#uc-38--view-the-home-summary)
- [UC-39 — Import a credit card statement](#uc-39--import-a-credit-card-statement)
- [UC-40 — Export data](#uc-40--export-data)


## UC-38 — View the home summary

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-07, UC-17, UC-36, UC-37, Glossary

### Objective

Allow an authenticated user to see, on a single screen when they open the
system, the state of their money — balances, available limits, the current
accounting month — together with what is coming up and the reminders that
need their attention.

### Actors

- **User** (primary) — the owner of the accounts, cards and months.
- **System** (secondary) — gathers the figures and the reminders.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Response

| Field             | Type          | Notes                                                                                   |
|-------------------|---------------|-----------------------------------------------------------------------------------------|
| `accounts`        | array         | Each bank account: `id`, `name`, `balance`, `projected_balance`                         |
| `total_balance`   | decimal(13,2) | Sum of the bank accounts' balances                                                      |
| `cards`           | array         | Each credit card: `id`, `name`, `limit`, `balance` (available limit)                    |
| `current_month`   | object        | `month`, `status`, `total_spent`, `total_received`, `by_category`, and the comparison with the previous month (UC-37 FR-13) |
| `upcoming`        | array         | Entries of the next 7 days: scheduled Pix, installments, recurring charges and receipts — `date`, `description`, `amount`, `source`, `kind` |
| `reminders`       | array         | The active reminders (FR-05): `type`, `message`, and a link to where it is resolved     |

### Functional requirements

- FR-01 — The home summary is the first screen after the user signs in, and
  can be reached from every screen.
- FR-02 — Balances and available limits are those of the moment of the
  request, as in UC-03 and UC-07. Removed entries never count; internal
  transfers change balances but no total (Glossary).
- FR-03 — The current accounting month shows its status, totals, spending
  per expense category and receipts per income category, and the comparison
  with the previous month (UC-37 FR-13). When the current month does not
  exist yet, the summary says so and offers to create it (UC-33).
- FR-04 — Upcoming entries are those whose date falls in the next 7 days,
  ordered by date: scheduled Pix (UC-17), installments, recurring charges
  and recurring receipts, and bill due dates. Stopped entries and removed
  months do not appear.
- FR-05 — Reminders are shown only inside the system, on the home summary.
  There are three kinds:
  - **pending month** — an open accounting month whose period has ended
    (UC-36 FR-08), linking to its consolidation;
  - **scheduled Pix** — a scheduled Pix (UC-17) due in the next 3 days,
    linking to its details;
  - **recurring entry made** — a recurring charge or receipt counted since
    the user's last visit, linking to the entry's details;
  - **bill due** — a bill due in the next 3 days, or overdue, linking to its
    payment (UC-30).
- FR-06 — A reminder disappears once what it refers to is resolved: the
  month consolidated, the Pix made or cancelled, the recurring entry seen, or
  the bill paid.
  The user cannot dismiss a pending month reminder without consolidating the
  month.
- FR-07 — Every figure links to its detail: an account to UC-03, a card to
  UC-07, the current month to UC-37, an upcoming entry to its details.

### Main flow

1. The user signs in, or chooses the home summary from any screen.
2. The system gathers the balances, limits, current month, upcoming entries
   and reminders (FR-02 to FR-05).
3. The system displays them.
4. The user may follow any figure or reminder to its detail (FR-07).

### Alternate flows

#### AF-01 — New user
Triggered at step 2 when the user has no bank account yet.

1. The system shows an empty summary that explains the first steps: create a
   bank account (UC-01), then register expenses and incomes.

### Exception flows

#### EF-01 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system shows no data and directs the user to authenticate.

### Acceptance criteria

- AC-01 — With accounts of `800.00` and `1200.00`, the summary shows each and
  a total balance of `2000.00`.
- AC-02 — A card with limit `5000.00` and balance `3800.00` shows `3800.00`
  available.
- AC-03 — The current month shows its total spent and received, spending per
  category, and the difference from the previous month.
- AC-04 — A Pix scheduled in 5 days and a recurring charge in 2 days appear in
  the upcoming entries; a stopped recurring expense does not.
- AC-05 — With August pending on 5 September, the summary shows a pending
  month reminder that links to consolidating August, and it disappears once
  August is consolidated.
- AC-06 — A Pix scheduled in 2 days shows a scheduled Pix reminder.
- AC-07 — A new user with no bank account sees the first steps.
- AC-08 — A bill due in 2 days shows a bill due reminder, which disappears
  once the bill is fully paid.


## UC-39 — Import a credit card statement

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-07, UC-11, UC-15, UC-33, Glossary

### Objective

Allow an authenticated user to register the purchases of a credit card
statement at once, from the file their bank provides, instead of one by one,
reviewing each line before anything is registered.

### Actors

- **User** (primary) — the owner of the card.
- **System** (secondary) — reads the file, prepares each line, detects
  duplicates, and registers the lines the user confirms.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns the credit card the statement belongs to.

### Post-conditions

- POST-01 — On success, every confirmed line is registered as a card purchase
  (UC-15), with the same effects on the card's balance and the accounting
  months as if registered by hand.
- POST-02 — On failure or cancellation, nothing is registered.

### Data

#### File

| Item          | Notes                                                                           |
|---------------|---------------------------------------------------------------------------------|
| Format        | Nubank statement (CSV), or any CSV whose columns the user maps (FR-11)          |
| Line fields   | Date, description, amount, and installment information, such as "Parcela 3/10" in Nubank files |

#### Review line

| Field              | Notes                                                                        |
|--------------------|------------------------------------------------------------------------------|
| `date`             | From the file                                                                |
| `description`      | From the file; editable                                                      |
| `value`            | From the file                                                                |
| `installment`      | `k/n` when the line is an installment                                        |
| `category_id`      | Suggested (FR-05); editable; required                                        |
| `accounting_month` | Defaults to the month chosen for the import (FR-04); editable               |
| `selected`         | Whether the line will be registered; `false` for duplicates and ignored lines |
| `note`             | Why a line is unselected: duplicate, payment, credit                         |

### Functional requirements

- FR-01 — Importing is started from a credit card's details (UC-07), for that
  card. The user chooses the file.
- FR-02 — The system reads every line and shows them all for review before
  registering anything. Nothing is registered until the user confirms.
- FR-03 — Each selected line is registered as a card purchase (UC-15): with
  its description, category, date, value and accounting month, and every
  rule of UC-15 — mandatory description and category, balance effects,
  "Changes to the past" and the past expense warning for consolidated
  months.
- FR-04 — The user chooses one accounting month for the whole import, by
  default the current one; it must exist, or the user is required to create
  it first (UC-33). Each line may be moved to another month in the review.
- FR-05 — The category of each line is suggested from the user's previous
  expenses with the same description; when there is none, "Other" is
  suggested. The user can change it.
- FR-06 — A line identified as installment *k* of *n*:
  - is a duplicate, and unselected, when the purchase is already registered
    with *n* installments on that card;
  - otherwise, is registered as a purchase of *n* installments of that
    amount, whose installment *k* falls in the chosen accounting month and
    whose first installment falls *k − 1* months earlier.
- FR-07 — A line is a duplicate, and unselected by default, when the card
  already has an expense with the same date, value and description, or when
  the same line appears in a file imported before. The user may select it
  anyway.
- FR-08 — A payment line (the payment of a previous bill) is offered to be
  registered as a bill payment (UC-30), from the card's bank account. A credit line (a refund or reversal) is offered to be
  registered as a refund (UC-32), with a suggested category. Neither is
  ever registered as a purchase.
- FR-09 — Imported purchases are registered as one-off or installment
  purchases; the import never creates a recurring expense.
- FR-10 — After registering, the system shows how many lines were
  registered as purchases, payments and refunds, skipped as duplicates, and
  ignored.
- FR-11 — For a generic CSV, the user maps its columns once per file: which
  one is the date, the description, the amount and, optionally, the
  installment. The system shows a preview of the first lines before reading
  the whole file.
- FR-12 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a credit card's details (UC-07), the user chooses to import a
   statement and selects the file.
2. The system reads the file.
   *(EF-01 if the file cannot be read.)*
3. The system asks for the accounting month of the import (FR-04).
   *(AF-01 if it does not exist.)*
4. The system shows the review list: every line with its suggested category
   and accounting month, duplicates and ignored lines unselected (FR-05 to
   FR-08).
5. The user reviews: changes descriptions, categories or months, and selects
   or unselects lines.
6. The user confirms.
   *(AF-02 if any selected line belongs to a consolidated month; if the user
   cancels, nothing is registered.)*
7. The system validates every selected line.
   *(EF-02 if a line is invalid.)*
8. The system registers the selected lines (FR-03) and shows the result
   (FR-10).
9. The user returns to the card's details, which show the new balance and
   expenses.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen month does not exist.

1. The system requires the user to create it (UC-33), then continues at
   step 4; if the user does not, nothing is imported.

#### AF-02 — Lines in consolidated months
Triggered at step 6 when a selected line belongs to a consolidated month.

1. The system shows the past expense warning (UC-19 FR-12), naming the
   months.
2. If the user acknowledges, the flow continues at step 7; otherwise it
   returns to step 5.

### Exception flows

#### EF-01 — Unreadable file
Triggered at step 2 when the file is not in a supported format or cannot be
read.

1. The system registers nothing and displays "This file could not be read;
   supported formats: <formats>", and the flow ends.

#### EF-02 — Invalid line
Triggered at step 7 when a selected line has no description or category, or
an invalid value or date.

1. The system registers nothing, marks the invalid lines, and the flow
   returns to step 5.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system registers nothing and directs the user to authenticate.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-12).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Importing a Nubank CSV with 20 purchases shows 20 lines for review
  and registers nothing until confirmed.
- AC-02 — After confirming, each selected line is a card purchase with its
  description, category and date, and the card's balance reflects them.
- AC-03 — A line "Corner Market" is suggested the category of the user's last
  "Corner Market" expense; an unknown description is suggested "Other".
- AC-04 — Importing the same file twice leaves every line of the second
  import unselected as a duplicate.
- AC-05 — A line marked as installment 3 of 10 ("Parcela 3/10") of `100.00`
  in the October import, not yet registered, creates a purchase of 10
  installments of `100.00` whose first installment is in August.
- AC-06 — A payment line can be registered as a bill payment from the card's
  bank account, and a refund line as a refund; neither becomes a purchase.
- AC-07 — An unreadable file registers nothing.
- AC-08 — A line without a category blocks the confirmation until one is
  chosen.
- AC-09 — A CSV from another bank is read after the user maps its date,
  description and amount columns.


## UC-40 — Export data

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-20, UC-27, UC-37

### Objective

Allow an authenticated user to take their data out of the system as a
spreadsheet or a PDF: a list of expenses or incomes, or an accounting month.

### Actors

- **User** (primary) — the owner of the data.
- **System** (secondary) — produces the file.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. The user receives a file.

### Data

| Parameter | Type | Required | Constraints                                               |
|-----------|------|----------|-----------------------------------------------------------|
| `what`    | text | Yes      | `expenses`, `incomes` or `accounting_month`               |
| `filters` | —    | No       | For lists: the filters of UC-20 or UC-27                  |
| `month`   | text | For a month | `YYYY-MM`                                              |
| `format`  | text | Yes      | `xlsx`, `csv` or `pdf`                                    |

### Functional requirements

- FR-01 — The user can export:
  - the expenses list (UC-20), with its current filters;
  - the incomes list (UC-27), with its current filters;
  - an accounting month (UC-37): its charges, receipts, totals per category,
    comparison with the previous month and, when consolidated, its closing
    with corrections.
- FR-02 — A list export contains every matching entry, not only the current
  page, with the same fields as the list.
- FR-03 — Only the user's own data is exported; removed entries never are.
- FR-04 — Exporting is started from the expenses list, the incomes list or an
  accounting month's view.

### Main flow

1. On a list or an accounting month, the user chooses to export and picks a
   format.
2. The system validates the request.
   *(EF-01 if it is invalid.)*
3. The system produces the file with the data as it stands (FR-01 to FR-03).
4. The user receives the file.

### Exception flows

#### EF-01 — Invalid request
Triggered at step 2 when `what`, `format`, the filters or the month are
invalid.

1. The system produces no file and names the problem.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system produces no file and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Exporting the expenses list filtered by "Groceries" in September as
  `xlsx` produces every matching expense, across all pages.
- AC-02 — Exporting a consolidated month as `pdf` includes its closing and
  corrections.
- AC-03 — A removed expense never appears in an export.

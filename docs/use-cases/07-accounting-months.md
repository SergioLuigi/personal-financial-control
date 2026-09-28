# Accounting months — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-33 to UC-37 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-33 — Create an accounting month](#uc-33--create-an-accounting-month)
- [UC-34 — Delete an accounting month](#uc-34--delete-an-accounting-month)
- [UC-35 — Set the accounting month closing day](#uc-35--set-the-accounting-month-closing-day)
- [UC-36 — Consolidate an accounting month](#uc-36--consolidate-an-accounting-month)
- [UC-37 — View an accounting month](#uc-37--view-an-accounting-month)


## UC-33 — Create an accounting month

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-34, UC-35, UC-36, UC-37, Glossary

### Objective

Allow an authenticated user to create an accounting month, so that expenses,
and incomes can be assigned to it.

### Actors

- **User** (primary) — the owner of the accounting months.
- **System** (secondary) — validates and creates the month.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the accounting month exists and is open.
- POST-02 — On failure, nothing changes.

### Data

| Field   | Type | Required | Constraints                                            |
|---------|------|----------|--------------------------------------------------------|
| `month` | text | Yes      | `YYYY-MM`; not already created                         |

### Functional requirements

- FR-01 — There is at most one accounting month per calendar month, so at
  most 12 per year. Creating a month that already exists changes nothing and
  succeeds.
- FR-02 — A created month is open (Glossary). Its period follows the closing
  day (UC-35 FR-03).
- FR-03 — Any month may be created — past, current or future — and months
  need not be created in order.
- FR-04 — Whenever the user must choose an accounting month — registering or
  changing an expense (UC-15, UC-16, UC-17, UC-19), an income
  (UC-24, UC-26), a refund (UC-32) or a bill payment (UC-30) — and the chosen
  month does not exist, the user is required to create it before continuing.
  Creating it is part of that flow and returns to it.
- FR-05 — A month can also be created on its own, from the accounting month
  view (UC-37).
- FR-06 — The first accounting month the user creates — normally when
  registering the first expense — is the start of their sequence of months.

### Main flow

1. The user chooses a month that does not exist, either while choosing an
   accounting month in another flow (FR-04) or from the accounting month view
   (UC-37).
2. The system asks the user to create it, showing its period.
3. The user confirms.
   *(If the user cancels, nothing changes and the calling flow does not
   continue with that month.)*
4. The system validates the month.
   *(EF-01 if it is invalid.)*
5. The system creates it, open, and confirms: "<month> created".
6. The user returns to the calling flow with the month chosen, or to the
   month's view.

### Exception flows

#### EF-01 — Invalid month
Triggered at step 4 when `month` is not a valid `YYYY-MM`.

1. The system creates nothing.
2. The system rejects the request, and the flow returns to step 2.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system creates nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Registering the first expense of a new user in `2026-09` requires
  creating `2026-09` first; it becomes the user's first accounting month.
- AC-02 — Creating `2026-10` when it already exists succeeds and changes
  nothing; there is never more than one `2026-10`.
- AC-03 — `2026-12` can be created before `2026-10` and `2026-11`.
- AC-04 — Choosing `2026-11` in an expense form when it does not exist
  requires creating it, then returns to the form with `2026-11` chosen.
- AC-05 — `2026-13` is rejected with EF-01.
- AC-06 — An unauthenticated request creates nothing.


## UC-34 — Delete an accounting month

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-33, UC-37, Glossary

### Objective

Allow an authenticated user to delete an accounting month created by mistake,
as long as nothing belongs to it.

### Actors

- **User** (primary) — the owner of the accounting months.
- **System** (secondary) — checks that the month is empty and deletes it.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the month is deleted physically and may be created
  again later (UC-33).
- POST-02 — On failure or cancellation, nothing changes.

### Data

| Parameter | Type | Required | Constraints       |
|-----------|------|----------|-------------------|
| `month`   | text | Yes      | `YYYY-MM`; exists |

### Functional requirements

- FR-01 — A month can be deleted only when nothing belongs to it: no charge
  of any expense — including installments and recurring charges assigned to
  it in advance — and no receipt of any income.
  Removed entries do not count.
- FR-02 — An empty month is deleted physically, open or consolidated; a
  consolidated one loses its closing.
- FR-03 — Before deleting, the system asks for confirmation.
- FR-04 — Deleting is started from the month's view (UC-37).

### Main flow

1. On an accounting month's view (UC-37), the user chooses to delete it.
2. The system checks that it is empty (FR-01).
   *(EF-01 if it is not.)*
3. The system asks for confirmation.
   *(If the user cancels, nothing changes.)*
4. The system deletes the month and confirms: "<month> deleted".
5. The user stays on the month's view, which now shows it as not created.

### Exception flows

#### EF-01 — Month not empty
Triggered at step 2 when anything belongs to the month.

1. The system changes nothing and displays "<month> cannot be deleted: it has
   <n> entries", with a way to see them.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — `2027-05`, created by mistake and empty, is deleted, and can be
  created again.
- AC-02 — A month holding the 3rd installment of a purchase cannot be
  deleted.
- AC-03 — A month whose only entry was removed can be deleted.


## UC-35 — Set the accounting month closing day

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-36, UC-37, Glossary

### Objective

Allow an authenticated user to set the day of the month on which their
accounting months are expected to end, which defines the period of every
accounting month.

### Actors

- **User** (primary) — the owner of the accounting months.
- **System** (secondary) — validates and stores the closing day.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the closing day is stored and defines the period of
  every open accounting month (Glossary).
- POST-02 — On failure, nothing changes.

### Data

| Field         | Type    | Required | Constraints                                          |
|---------------|---------|----------|------------------------------------------------------|
| `closing_day` | integer | Yes      | 1–31; defaults to 31 (the last day of every month)   |

### Functional requirements

- FR-01 — Each user has one closing day, shared by all their accounting
  months, bank accounts and credit cards.
- FR-02 — Until the user sets one, the closing day is the last day of each
  month.
- FR-03 — Accounting month `YYYY-MM` runs from the day after the previous
  month's closing day to its own closing day. In a month shorter than the
  closing day, the period ends on the month's last day.
- FR-04 — Changing the closing day changes the periods of open and
  not-yet-created accounting months only. Consolidated months keep the period they had when consolidated.
- FR-05 — Changing the closing day never moves a charge to another accounting
  month: every charge keeps the accounting month stored with it (Glossary).
  It changes which month is the current one, and so the default month for new
  expenses.
- FR-06 — Setting the same closing day again changes nothing and succeeds.

### Main flow

1. The user opens the accounting month settings.
2. The system shows the current closing day.
3. The user enters a new closing day and confirms.
4. The system validates it.
   *(EF-01 if it is invalid.)*
5. The system stores it and confirms: "Accounting months now close on day
   <closing day>".

### Exception flows

#### EF-01 — Invalid closing day
Triggered at step 4 when the closing day is missing, not an integer, or
outside 1–31.

1. The system changes nothing.
2. The system displays "Closing day must be between 1 and 31", and the flow
   returns to step 3.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — A user who never set a closing day has accounting month `2026-09`
  running from 1 to 30 September.
- AC-02 — With closing day 25, accounting month `2026-09` runs from
  26 August to 25 September, and on 27 September the current accounting month
  is `2026-10`.
- AC-03 — With closing day 30, accounting month `2027-02` ends on the last
  day of February.
- AC-04 — Changing the closing day from 31 to 25 leaves every existing charge
  in its accounting month and does not change the period of any consolidated
  month.
- AC-05 — `0`, `32` and `10.5` are each rejected with EF-01.


## UC-36 — Consolidate an accounting month

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-19, UC-21, UC-35, UC-37, UC-38, Glossary

### Objective

Allow an authenticated user to state that an accounting month is closed,
whenever they decide — before or after its closing day — so that the system
takes a snapshot of it, its closing, and protects the current balances from
later corrections to it.

### Actors

- **User** (primary) — the owner of the accounting months.
- **System** (secondary) — reminds the user of pending months, takes the
  snapshot and marks the month as consolidated.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the month is consolidated and its closing is stored.
  From then on, changes to its charges correct its closing and never the
  current balance of any funding source (Glossary, "Changes to the past").
- POST-02 — On failure or cancellation, nothing changes.

### Data

#### Request

| Parameter | Type | Required | Constraints                                 |
|-----------|------|----------|---------------------------------------------|
| `month`   | text | Yes      | `YYYY-MM`; the earliest open created accounting month |

#### Closing (stored)

| Field                | Type          | Notes                                                                 |
|----------------------|---------------|-----------------------------------------------------------------------|
| `month`              | text          | `YYYY-MM`                                                             |
| `period`             | object        | `from` and `to` dates of the month when consolidated                  |
| `consolidated_on`    | date          | When the user consolidated it                                         |
| `total_spent`        | decimal(13,2) | Sum of the month's charges, internal transfers excluded               |
| `by_category`        | array         | Total spent per category                                              |
| `total_received`     | decimal(13,2) | Sum of the month's receipts (UC-24), internal transfers excluded      |
| `received_by_category` | array       | Total received per income category                                    |
| `bill_payments`      | array         | The month's bill payments: card, bill, bank account, amount — counted in no total |
| `bills`              | array         | Each card's bill for the month: total, due date, status |
| `transfers`          | array         | The month's internal transfers, with source and destination           |
| `sources`            | array         | Per bank account and credit card: `id`, `name`, amount spent and received in the month, balance at consolidation |
| `corrections`        | array         | Changes made after consolidation: date, expense, amount change (UC-19, UC-21) |

### Functional requirements

- FR-01 — The system consolidates months only for the user who owns them.
- FR-02 — Months are consolidated in order: only the earliest open created
  month can be consolidated. A month cannot be consolidated while an earlier
  created month is still open. A month that was never created is skipped.
- FR-03 — The user decides when to consolidate. A month may be consolidated
  before its closing day has arrived, or at any time after it.
- FR-04 — Consolidation does not change the month's period (UC-35 FR-03),
  and it does not move any charge to another month.
- FR-05 — Consolidating takes the closing: the month's total spent, total
  received and, for each bank account and credit
  card, the amount spent and received in the month and the balance at that
  moment.
- FR-06 — A closing can be corrected. Changing or removing a charge of a
  consolidated month (UC-19, UC-21), or registering a new expense, income,
  refund or bill payment in it, updates its closing and records the
  correction in the closing's `corrections`, keeping the original
  consolidation date. Changes and removals never change the current
  balances; new entries do, as money that really moved (Glossary, "Changes
  to the past").
- FR-07 — A consolidated month is never reopened; it can only be corrected
  (FR-06).
- FR-08 — Every open accounting month whose period has ended is pending. The
  system reminds the user of each pending month on the home summary (UC-38
  FR-05), but never consolidates a month on its own. While a month is
  pending, later months are locked (Glossary, "Pending month lock").
- FR-09 — Once the current month is consolidated, new expenses default to
  the next open month (Glossary, "Accounting month of a charge").
- FR-10 — Before consolidating, the system shows the month's total spent and
  the amount per funding source, and asks the user to confirm.
- FR-11 — Consolidating a month that is already consolidated changes nothing
  and succeeds, keeping the original closing.
- FR-12 — When a month is consolidated before its period ends, charges of
  that month whose date arrives after the consolidation — such as a
  recurring charge or an installment — still happen: each changes the current
  balance of its funding source normally and is recorded as a correction in
  the month's closing (Glossary, "Changes to the past").
- FR-13 — Before consolidating a month early, the system lists the charges
  of that month still to come (FR-12), so the user knows they will be added
  as corrections.

### Main flow

1. The system reminds the user of a pending month (FR-08), or the user opens
   an accounting month (UC-37) and chooses to consolidate it.
2. The system shows the month's total spent and received and the amount per
   funding source, and asks for confirmation (FR-10).
   *(AF-01 if the month's period has not ended yet.)*
3. The user confirms.
   *(If the user cancels, nothing changes and the flow ends.)*
4. The system validates that the month is the earliest open month.
   *(EF-01 if it is not.)*
5. The system takes the closing (FR-05), marks the month as consolidated and
   confirms: "<month> consolidated".
6. The user returns to the accounting month (UC-37), which shows it as
   consolidated with its closing.

### Alternate flows

#### AF-01 — Early consolidation
Triggered at step 2 when the month's period has not ended yet.

1. The system also lists the charges and receipts of the month still to come
   (FR-13), stating that they will change the current balance and be recorded
   as corrections when their dates arrive (FR-12).
2. The flow continues at step 3.

### Exception flows

#### EF-01 — Not the earliest open month
Triggered at step 4 when an earlier month is still open, or the month is
malformed.

1. The system changes nothing.
2. The system displays "Consolidate <earliest open month> first", and the
   flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — With closing day 30 and August open on 5 September, the system
  reminds the user that August is pending, and August stays open until the
  user consolidates it.
- AC-02 — Consolidating August on 3 September stores its total spent, the
  amount and balance per funding source, and `consolidated_on` 3 September.
- AC-03 — Consolidating September on 20 September, before its closing day,
  succeeds; an expense registered on 22 September then defaults to October.
- AC-04 — Consolidating September while August is open fails with EF-01.
- AC-05 — With August consolidated, removing a `200.00` expense of August
  adds a correction to the August closing, lowers its total spent by
  `200.00`, and leaves every current balance unchanged.
- AC-06 — With August still open, the same removal changes the current
  balance of the expense's funding source.
- AC-07 — Consolidating August again keeps the original closing and
  consolidation date.
- AC-08 — Consolidation asks for confirmation; cancelling leaves the month
  open.
- AC-09 — September consolidated on 20 September, with a `50.00` recurring
  Pix of September dated the 22nd: on the 22nd the account's current balance
  drops by `50.00`, and the September closing records it as a correction.
- AC-10 — Consolidating September early lists the September charges still to
  come before asking for confirmation.
- AC-11 — With September and November created and October never created,
  after September is consolidated the earliest open month is November.
- AC-12 — The closing records the month's incomes
  alongside its expenses.
- AC-13 — A `1000.00` internal transfer in September appears in the
  September closing's transfers and is counted in neither the total spent
  nor the total received.
- AC-14 — The closing shows the total spent per category.
- AC-15 — With August pending, registering an expense in September fails
  until August is consolidated; registering one in August succeeds.


## UC-37 — View an accounting month

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-18, UC-35, UC-36, UC-38, UC-40, Glossary

### Objective

Allow an authenticated user to see any accounting month: past and current
months as history, and future months as a forecast built from the expenses
already registered for them.

### Actors

- **User** (primary) — the owner of the accounting months.
- **System** (secondary) — gathers the month's charges and totals.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter      | Type    | Required | Constraints                                        |
|----------------|---------|----------|----------------------------------------------------|
| `month`        | text    | No       | `YYYY-MM`; defaults to the current accounting month |
| `charges_page` | integer | No       | ≥ 0, defaults to `0` (the first page)              |

#### Response

| Field             | Type          | Notes                                                                 |
|-------------------|---------------|-----------------------------------------------------------------------|
| `month`           | text          | `YYYY-MM`                                                             |
| `period`          | object        | `from` and `to` dates (UC-35 FR-03)                                   |
| `kind`            | text          | `history` (past or current month) or `forecast` (future month)        |
| `status`          | text          | `not_created`, `open`, `pending` (open, period ended) or `consolidated` |
| `closing`         | object        | Consolidated months only: the closing (UC-36), with its corrections   |
| `total`           | decimal(13,2) | Sum of the month's charges, internal transfers excluded               |
| `by_category`     | array         | Total of the month's charges per expense category, and of its receipts per income category |
| `total_received`  | decimal(13,2) | Sum of the month's incomes (UC-24)                                |
| `sources`         | array         | Per bank account and credit card: `id`, `name`, total of the month's charges and incomes |
| `incomes`         | array         | The month's incomes: `id`, `description`, `date`, `value`, `bank_account` |
| `charges`         | page          | `items` (expense `id`, `description`, `category`, `date`, `amount`, `installment` such as `3/12`, `recurring`, `payment_method`, `recipient_name`, `source`, `internal_transfer`; 0–10), `page`, `total_items`, `total_pages` |
| `bills`           | array         | Each card's bill for the month: `card`, `total`, `due_date`, `status`, `outstanding` |
| `pending_months`  | array         | Every pending month (UC-36 FR-08), for the reminder                   |
| `comparison`      | object        | Against the previous month: total spent, total received and each category's total, with the difference in value and percentage |

### Functional requirements

- FR-01 — The system shows only the requesting user's accounting months and
  charges.
- FR-02 — A month shows every charge that belongs to it (Glossary,
  "Accounting month of a charge") — one-off expenses, installments and
  recurring charges, from every funding source and payment method — whatever
  the date of the charge.
- FR-03 — Removed expenses and removed months never appear (Glossary).
  Stopped recurring expenses have no charge in the months in which they are
  stopped.
- FR-04 — A future month is a forecast: it shows the installments and
  recurring charges that will fall in it and the expenses registered for it,
  as the data stands today. The forecast is recalculated on every request,
  so it follows every change, removal, stop and resumption.
- FR-05 — A recurring expense that is active appears in every future month
  requested, with no end, until it is stopped or removed.
- FR-06 — A consolidated month shows its closing, including the corrections
  made after consolidation (UC-36 FR-06).
- FR-07 — The charges are ordered by date ascending with the expense's `id`
  as the final tie-breaker, at most 10 per page.
- FR-08 — The user can move to the previous or the next accounting month, or
  jump to any month, without limit in either direction.
- FR-09 — Selecting a charge opens its expense's details (UC-18).
- FR-10 — The accounting month view is reached from its own menu, which opens
  on the current accounting month.
- FR-11 — A month that was never created can still be viewed: it is shown as
  not created, with the forecast of the charges that will fall in it, and
  the user can create it from there (UC-33).
- FR-12 — A month shows its incomes (UC-24) alongside its charges, with
  the total received. Receipts of recurring incomes appear in every month in
  which they fall, including the forecast of future months, following the
  same rules as recurring charges (FR-03 to FR-05).
- FR-13 — A month is compared with the previous accounting month: total
  spent, total received and each category's total, with the difference in
  value and in percentage. When the previous month has no data, the
  comparison is empty. Totals per category are also shown as a chart.

### Main flow

1. The user opens the accounting months menu.
2. The system validates the requested month and page.
   *(EF-01 if either is invalid.)*
3. The system gathers the month's charges (FR-02 to FR-05) and totals.
4. The system displays the month's period, kind, status, total, totals per
   funding source, the requested page of charges and, for a consolidated
   month, its closing. When there are pending months, it reminds the user of
   them.
5. The user may move to another month or page, returning the flow to step 2;
   select a charge, leading to UC-18; select an income, leading to
   UC-25; create the month when it does not exist, leading to
   UC-33; delete the month when it is empty, leading to UC-34; export it,
   leading to UC-40; or, for the earliest open month, consolidate it,
   leading to UC-36.

### Exception flows

#### EF-01 — Invalid month or page
Triggered at step 2 when `month` is not a valid `YYYY-MM`, or `charges_page`
is negative or not an integer.

1. The system does not return the month.
2. The system rejects the request, naming the offending parameter, and the
   flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no data whatsoever and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — Opening the menu shows the current accounting month, as history,
  with its total and totals per funding source.
- AC-02 — A card purchase of `1200.00` in 12 installments assigned to
  September shows a `100.00` charge (`1/12`) in September and `3/12` in
  November; November is shown as a forecast.
- AC-03 — An active recurring Pix of `50.00` appears in December 2027 as a
  forecast; after it is stopped, it no longer appears in any future month;
  after it is resumed, it appears again from its next charge.
- AC-04 — A removed month of a recurring expense does not appear in that
  month.
- AC-05 — A purchase dated 29 September and assigned to October appears in
  October, not in September.
- AC-06 — A consolidated month shows its closing and every correction made
  after consolidation.
- AC-07 — `month=2026-13` and `charges_page=-1` are each rejected with EF-01.
- AC-08 — With August pending, every month view reminds the user of it.
- AC-09 — A month that was never created shows `status` `not_created`, its
  forecast charges, and the option to create it.
- AC-10 — A month with a `5000.00` salary income shows it with
  `total_received` `5000.00`.
- AC-11 — The month shows its total per category, and internal transfers are
  listed but counted in no total.
- AC-12 — With `1200.00` spent on "Groceries" in August and `1500.00` in
  September, September shows "Groceries" `+300.00` (`+25%`) against August.
- AC-13 — The month shows each card's bill with its total, due date and
  status; bill payments are listed but counted in no total.

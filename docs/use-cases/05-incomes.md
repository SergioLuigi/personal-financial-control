# Incomes — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-24 to UC-28 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-24 — Register an income](#uc-24--register-an-income)
- [UC-25 — View income details](#uc-25--view-income-details)
- [UC-26 — Update an income](#uc-26--update-an-income)
- [UC-27 — List incomes](#uc-27--list-incomes)
- [UC-28 — Remove an income](#uc-28--remove-an-income)


## UC-24 — Register an income

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-22, UC-23, UC-25, UC-27, UC-28, Glossary

### Objective

Allow an authenticated user to register money received into one of their own
bank accounts — once, or every month, such as a salary — so that the
account's balance and the accounting months reflect it.

### Actors

- **User** (primary) — the owner of the bank account.
- **System** (secondary) — validates the income, enforces ownership, persists
  it and raises the account's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns at least one bank account.

### Post-conditions

- POST-01 — On success, an income is persisted, associated with the chosen
  bank account and accounting month, and the account's balance reflects it.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field              | Type          | Required | Constraints                                                   |
|--------------------|---------------|----------|---------------------------------------------------------------|
| `bank_account_id`  | text          | Yes      | Must match a bank account the user owns (UC-03)               |
| `description`      | text          | Yes      | 1–100 chars, trimmed; what the income is, such as "Salary"    |
| `category_id`      | text          | Yes      | One of the user's income categories (UC-12)                   |
| `date`             | date          | Yes      | Date of the receipt, or of the first one; not in the future   |
| `value`            | decimal(13,2) | Yes      | Greater than `0.00`; the amount of each receipt               |
| `recurring`        | boolean       | No       | Defaults to `false`                                           |
| `accounting_month` | text          | No       | `YYYY-MM`; defaults per the Glossary                          |

### Functional requirements

- FR-01 — The system persists a new income received into exactly one bank
  account owned by the requesting user. An account may have any number of
  incomes.
- FR-02 — An income raises the account's balance:
  - a one-off income by its `value`, on registration;
  - a recurring income by its `value` once for every monthly receipt whose
    date has been reached, except removed months (UC-28) and months in which
    it was stopped (UC-22, UC-23).
  Changes to receipts that belong to a consolidated accounting month correct
  that month's closing and never the current balance (Glossary, "Changes to
  the past").
- FR-03 — An income belongs to an accounting month, chosen by the user; by
  default the current one (Glossary). The month must exist, or the user is
  required to create it first (UC-33). Choosing a consolidated month
  shows the past entry warning; as a new entry, the income raises the
  current balance normally and is recorded as a correction in that month's
  closing (Glossary, "Changes to the past").
- FR-04 — Registering an income is started from the bank account's details
  (UC-03), with that account preselected.
- FR-05 — Any `bank_account_id` that does not match an account the user owns
  produces the same "not found" response as UC-03 FR-02.
- FR-06 — A recurring income is received on the same day every month from
  `date` onwards; in a month shorter than that day, on its last day (as in
  UC-15 FR-05). Receipt *k* belongs to the *k − 1*-th accounting month after
  the first one; those later months do not need to exist yet (Glossary).
- FR-07 — A recurring income has no end: it is received every month until it
  is stopped (UC-22) or removed (UC-28), and it appears in the forecast of
  every future month (UC-37).
- FR-08 — When a recurring income starts in an accounting month earlier than
  the current one, the system asks whether to include the past months, as
  for a recurring expense (UC-15 FR-16).
- FR-09 — Money moved from another of the user's accounts is an internal
  transfer registered as a Pix (UC-16 FR-14), not an income.
- FR-10 — Every income has exactly one income category, which is mandatory
  (UC-11 FR-03). The default income categories are always available
  (UC-11 FR-05).
- FR-11 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a bank account's details (UC-03), the user chooses to register an
   income.
2. The system presents an empty income form with that account and the
   current accounting month preselected.
3. The user supplies a description, an income category, a date and a value,
   and optionally marks
   the income as recurring or chooses another accounting month.
   *(AF-01 if the chosen month does not exist.)*
4. The user confirms.
   *(AF-02 if the chosen accounting month is consolidated; AF-03 if the
   income is recurring and starts in an earlier month.)*
5. The system retrieves the account, restricted to accounts the user owns.
   *(EF-01 if no such account is found.)*
6. The system validates the submitted data.
   *(EF-02 if the description is invalid; EF-03 if the date is invalid; EF-04
   if the value is invalid; EF-06 if the category is invalid.)*
7. The system persists the income, raises the account's balance (FR-02), and
   confirms: "Income <description> registered".
8. The user returns to the account's details (UC-03), which show the new
   balance.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing.
2. Once it is created, the flow continues at step 3; if the user does not
   create it, nothing is registered.

#### AF-02 — Consolidated month
Triggered at step 4 when the chosen accounting month is consolidated.

1. The system shows the past entry warning (UC-19 FR-12), naming the month:
   as a new entry, it changes the current balance and is recorded as a
   correction in that month's closing (Glossary, "Changes to the past").
2. If the user acknowledges, the flow continues; if not, it returns to
   step 3 and nothing is registered.

#### AF-03 — Recurring income starting in the past
Triggered at step 4 when the income is recurring and its first receipt falls
in an accounting month earlier than the current one.

1. The system asks whether to include the past months (FR-08), stating how
   many months and how much they add up to.
2. The user chooses, and the flow continues at step 5 accordingly.

### Exception flows

#### EF-01 — Bank account not found
Triggered at step 5 when no account the user owns has that id.

1. The system does not persist the income.
2. The system displays "Bank account not found" and offers a way back to the
   bank accounts list, and the flow ends.

#### EF-02 — Invalid description
Triggered at step 6 when the description is empty after trimming, or longer
than 100 characters.

1. The system does not persist the income.
2. The system displays the corresponding message next to the description
   field, and the flow returns to step 3.

#### EF-03 — Invalid date
Triggered at step 6 when the date is missing, not a valid date, or later than
today.

1. The system does not persist the income.
2. The system displays the corresponding message next to the date field, and
   the flow returns to step 3.

#### EF-04 — Invalid value
Triggered at step 6 when the value is missing, not a valid amount with at
most two decimal places, or not greater than `0.00`.

1. The system does not persist the income.
2. The system displays "Value must be a positive amount" next to the value
   field, and the flow returns to step 3.

#### EF-05 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

#### EF-06 — Invalid category
Triggered at step 6 when no category is chosen, or it is not one of the
user's income categories.

1. The system does not persist the income.
2. The system displays "Choose a category" next to the category field, and
   the flow returns to step 3.

#### EF-07 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-11).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Registering a `5000.00` "Salary" into an account with balance
  `200.00` makes the balance `5200.00`.
- AC-02 — An income registered without an accounting month belongs to the
  current one.
- AC-03 — Registering a new income in consolidated August shows the past
  entry warning; once confirmed, it raises the current balance and is
  recorded as a correction in the August closing.
- AC-04 — An empty description, a value of `0.00` and a date later than today
  each fail with the matching exception.
- AC-05 — Using another user's account, a non-existent id or `abc` produces
  the identical EF-01 response.
- AC-06 — An unauthenticated request persists nothing.
- AC-07 — A recurring "Salary" of `5000.00` dated 5 September raises the
  balance by `5000.00` on registration, and by `5000.00` more on 5 October,
  5 November and every month after.
- AC-08 — A recurring income registered with only its first month created
  can be registered; its later receipts appear in the forecast of the
  following months.
- AC-09 — A new user can register an income in "Salary" without creating any
  category; an income without a category, or with an expense category, fails
  with EF-06.


## UC-25 — View income details

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-22, UC-23, UC-24, UC-26, UC-27, UC-28

### Objective

Allow an authenticated user to see the full details of one of their own
incomes, including, for a recurring income, every month in which it is
received.

### Actors

- **User** (primary) — the owner of the bank account and of the income.
- **System** (secondary) — retrieves the income and enforces ownership.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter | Type | Required | Constraints                       |
|-----------|------|----------|-----------------------------------|
| `id`      | text | Yes      | Any string; no format is enforced |

#### Response

| Field              | Type          | Notes                                         |
|--------------------|---------------|-----------------------------------------------|
| `id`               | text          | The income's id                               |
| `bank_account`     | object        | `id` and `name` of the account                |
| `description`      | text          | As stored                                     |
| `category`         | object        | `id` and `name`                               |
| `date`             | date          | As stored                                     |
| `value`            | decimal(13,2) | As stored                                     |
| `accounting_month` | text          | As stored; for a recurring income, the month of the first receipt |
| `recurring`        | boolean       | As stored                                     |
| `status`           | text          | `active` or `stopped` (Glossary)              |
| `next_receipt_date`| date          | Recurring only: the next receipt; empty while stopped |
| `stop_periods`     | array         | Recurring only: `stopped_on` and `resumed_on` of each stop |
| `months`           | array         | Recurring only: `accounting_month`, `date`, `amount` and `situation` (`received`, `removed`, `stopped` or `forecast`) of each month, from the first receipt to the next forecast one |

### Functional requirements

- FR-01 — The system returns an income only to the user who owns its bank
  account.
- FR-02 — Any id that does not match an income on an account the user owns —
  including a removed income — produces the same "not found" response.
- FR-03 — The details are reached from the account's details (UC-03), the
  incomes list (UC-27) or an accounting month (UC-37), among other places
  such as the home summary (UC-38), and the user returns to where they came
  from.
- FR-04 — A recurring income shows its month history, as a recurring expense
  does (UC-18 FR-08): each month marked as received, removed, stopped or
  forecast, with its amount. A stopped income is marked as stopped, with its
  stop periods.

### Main flow

1. The user selects an income on an account's details, the incomes list or
   an accounting month.
2. The system retrieves the income, restricted to the user's accounts.
   *(EF-01 if no such income is found.)*
3. The system displays the income.
4. The user returns to where they came from; or edits the income, leading to
   UC-26; removes it, leading to UC-28; or, for a recurring income, stops
   it, leading to UC-22, or resumes it when stopped, leading to UC-23.

### Exception flows

#### EF-01 — Income not found
Triggered at step 2 when no income on an account the user owns has that id.

1. The system returns no income data.
2. The system displays "Income not found" and offers a way back to the
   incomes list, and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no data and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Opening one of the user's incomes shows its account, description,
  date, value and accounting month exactly as stored.
- AC-02 — Another user's income, a removed income, a non-existent id and
  `abc` all produce the identical EF-01 response.
- AC-03 — An unauthenticated request returns no data.
- AC-04 — A recurring "Salary" from January, with May removed, viewed in
  September, shows January to April and June to September received, May
  removed, and October forecast.


## UC-26 — Update an income

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-25, Glossary

### Objective

Allow an authenticated user to correct the description, date, value and
accounting month of one of their own incomes through a partial update that is
idempotent.

### Actors

- **User** (primary) — the owner of the bank account and of the income.
- **System** (secondary) — validates the changes, enforces ownership,
  persists the income and recalculates the account's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the supplied fields hold the new values, every other
  field is unchanged, and the balance reflects the updated income.
- POST-02 — On failure, nothing is persisted.

### Data

#### Request

| Parameter          | Type          | Required | Constraints                                 |
|--------------------|---------------|----------|---------------------------------------------|
| `id`               | text          | Yes      | Identifies the income (as in UC-25)    |
| `description`      | text          | No       | 1–100 chars, trimmed                        |
| `category_id`      | text          | No       | An income category; cannot be cleared       |
| `date`             | date          | No       | Not in the future                           |
| `value`            | decimal(13,2) | No       | Greater than `0.00`                         |
| `accounting_month` | text          | No       | `YYYY-MM`; must exist                       |
| `recurring`        | boolean       | No       |                                             |
| `value_scope`      | text          | With `value` on a recurring income | `all_months` or `from_month` |
| `value_from_month` | text          | With `from_month` | `YYYY-MM`; first month with the new value |

The bank account and the `id` cannot be changed.

### Functional requirements

- FR-01 — The update follows the same rules as UC-19 FR-01 to FR-07: partial,
  absolute values, idempotent, and rejecting unchangeable fields such as
  `bank_account_id`.
- FR-02 — The account's balance is recalculated by the difference: in an open
  accounting month it changes the current balance; in a consolidated one it
  corrects that month's closing only (Glossary, "Changes to the past").
- FR-03 — Changing an income that belongs, before or after the change, to a
  consolidated month shows the past entry warning (as in UC-19 FR-12) before
  the update is sent.
- FR-04 — A new accounting month must exist, or the user is required to
  create it first (UC-33).
- FR-05 — The update is started from the income's details (UC-25).
- FR-06 — When the value of a recurring income changes, the user chooses
  whether it applies to all months, as a correction, or from a month on — for
  a raise, for example — keeping the value of earlier receipts, exactly as
  for a recurring expense (UC-19 FR-15).
- FR-07 — Changing `accounting_month` moves every receipt by the same number
  of months (UC-19 FR-14).
- FR-08 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On the income's details (UC-25), the user chooses to edit it.
2. The system presents a form pre-filled with its current values.
3. The user changes any of them; for a recurring income, changing the value
   also asks whether it applies to all months or from a month on (FR-06).
   *(AF-02 if a new accounting month is chosen that does not exist.)*
4. The user confirms.
   *(AF-01 if the income is in a consolidated month before or after the
   change.)*
5. The system retrieves and validates, as in UC-24.
   *(EF-01 if not found; EF-02 if an unchangeable field is supplied; the
   validation exceptions of UC-24 otherwise.)*
6. The system persists the changes, recalculates the balance (FR-02) and
   confirms: "Income updated".
7. The user returns to the income's details.

### Alternate flows

#### AF-01 — Past entry warning
Triggered at step 4 per FR-03.

1. The system shows the warning, naming the consolidated months involved.
2. If the user acknowledges, the flow continues at step 5; if not, nothing is
   sent.

#### AF-02 — Accounting month not created
Triggered at step 3 when the new accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing
   (FR-04); if the user does not, nothing is sent.

### Exception flows

#### EF-01 — Income not found
Triggered at step 5 when no income on an account the user owns has that id.

1. The system changes nothing and displays "Income not found".

#### EF-02 — Unchangeable field
Triggered at step 5 when the request contains a field other than those in
the Data table.

1. The system changes nothing and names the field.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-08).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Changing a `5000.00` income of the current month to `5500.00`
  raises the balance by `500.00`; sending it again changes nothing more.
- AC-02 — With August consolidated, changing an August income shows the past
  entry warning and corrects only the August closing.
- AC-03 — Sending `bank_account_id` fails with EF-02.
- AC-04 — In September, raising a `5000.00` recurring salary to `5500.00`
  from October keeps `5000.00` in every month up to September and receives
  `5500.00` from October on.


## UC-27 — List incomes

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-20, UC-25, UC-40

### Objective

Allow an authenticated user to browse all their own incomes, across every
bank account, ten at a time, from a dedicated incomes menu.

### Actors

- **User** (primary) — the owner of the accounts and of the incomes.
- **System** (secondary) — filters, orders and paginates the incomes.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter         | Type          | Required | Constraints                                   |
|-------------------|---------------|----------|-----------------------------------------------|
| `page`            | integer       | No       | ≥ 0, defaults to `0`                          |
| `bank_account_id` | text          | No       | Chosen from the user's own bank accounts      |
| `description`     | text          | No       | 0–100 chars, trimmed                          |
| `category_id`     | text          | No       | One of the user's income categories           |
| `from_date`       | date          | No       | Inclusive lower bound                         |
| `to_date`         | date          | No       | Inclusive upper bound; must be ≥ `from_date`  |
| `from_month`      | text          | No       | `YYYY-MM`; inclusive lower bound              |
| `to_month`        | text          | No       | `YYYY-MM`; must be ≥ `from_month`             |
| `min_value`       | decimal(13,2) | No       | Inclusive lower bound                         |
| `max_value`       | decimal(13,2) | No       | Inclusive upper bound; must be ≥ `min_value`  |
| `recurring`       | boolean       | No       | Omitted means both                            |
| `status`          | text          | No       | `active` or `stopped`; omitted means both     |

#### Response

A page of incomes (`id`, `bank_account` (`id`, `name`), `description`,
`category` (`id`, `name`), `date`, `value`, `accounting_month`,
`recurring`, `status`), with `page`, `total_items` and
`total_pages`, as in UC-20.

### Functional requirements

- FR-01 — The list follows the same rules as UC-20 FR-01 to FR-04 and FR-08
  to FR-12: only the user's incomes, 10 per page, most recent first with
  `id` as the tie-breaker, filters combined with AND, and empty results that
  are not errors.
- FR-02 — The user may filter by every income field except `id`: the account,
  the category, the recurring flag and the status exactly, the description as a
  case-insensitive substring, and the date, accounting month and value by
  inclusive ranges.
- FR-03 — Removed incomes never appear.
- FR-04 — The list is reached from its own incomes menu. Selecting an income
  opens its details (UC-25).
- FR-05 — The accounting month filter returns a recurring income when at
  least one of its receipts belongs to a month in the range, as for expenses
  (UC-20 FR-15). Stopped incomes appear, marked as stopped, unless excluded
  by the status filter.

### Main flow

1. The user opens the incomes list from the incomes menu.
2. The system validates the parameters.
   *(EF-01 if any is invalid.)*
3. The system returns the requested page of matching incomes.
4. The user may change page or filters, returning to step 2; select an
   income, leading to UC-25; or export the list with its current filters,
   leading to UC-40.

### Exception flows

#### EF-01 — Invalid parameters
Triggered at step 2 as in UC-20 EF-01.

1. The system does not return a page and names the offending parameter.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no data and directs the user to authenticate.

### Acceptance criteria

- AC-01 — A user with 25 incomes across two accounts receives 10 on page 0,
  most recent first, with `total_items` 25.
- AC-02 — `description=sal` returns "Salary" and "SALARY BONUS".
- AC-03 — `from_month=2026-09&to_month=2026-09` returns only the incomes of
  September.
- AC-04 — A removed income never appears.
- AC-05 — `from_month=2026-11&to_month=2026-11` returns a recurring salary
  started in January; `status=stopped` returns only stopped recurring
  incomes.


## UC-28 — Remove an income

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-21, UC-22, UC-25, Glossary

### Objective

Allow an authenticated user to remove one of their own incomes, after being
warned that the removal cannot be undone and changes the account's balance or
the closing of a consolidated month. For a recurring income, the user may
instead remove the receipt of a single accounting month.

### Actors

- **User** (primary) — the owner of the bank account and of the income.
- **System** (secondary) — warns the user, enforces ownership, removes the
  income logically and recalculates the balance.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the income is removed (Glossary): it no longer
  appears anywhere and no longer counts.
- POST-02 — On failure or cancellation, nothing changes.

### Data

| Parameter | Type | Required            | Constraints                                                    |
|-----------|------|---------------------|----------------------------------------------------------------|
| `id`      | text | Yes                 | Identifies the income (as in UC-25)                            |
| `scope`   | text | Recurring only      | `single_month` or `whole_income`                               |
| `month`   | text | With `single_month` | Accounting month, `YYYY-MM`, in which the income has a receipt |

### Functional requirements

- FR-01 — Removal follows UC-21 FR-01 to FR-03: only for the owner, logical,
  irreversible, and preceded by a warning.
- FR-02 — The warning states that the action cannot be undone and that it
  lowers the balance of the named bank account — or, for an income of a
  consolidated month, that it corrects that month's closing and not the
  current balance.
- FR-03 — Removing an income of an open month lowers the account's current
  balance by its value; removing one of a consolidated month corrects that
  month's closing only.
- FR-04 — Removing an income that is already removed produces the "not found"
  response.
- FR-05 — The removal is started from the income's details (UC-25).
- FR-06 — For a recurring income, the system asks, before the warning,
  whether to remove a single month or the whole income, with the same rules
  as a recurring expense (UC-21 FR-05 to FR-08): the current month is
  offered by default; any month with a receipt, past or future, may be
  chosen; a past consolidated month corrects its closing; a future month
  prevents that receipt; and the user is reminded that the income can be
  stopped instead (UC-22).
- FR-07 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On the income's details, the user chooses to remove it.
   *(AF-01 if the income is recurring.)*
2. The system shows the warning (FR-02).
3. The user confirms.
   *(If the user cancels, nothing changes.)*
4. The system retrieves the income, restricted to the user's accounts, and
   validates the scope and month of a recurring income.
   *(EF-01 if not found; EF-03 if the scope or month is invalid.)*
5. The system removes it logically, recalculates the balance (FR-03) and
   confirms: "Income removed".
6. The user returns to where they opened the income from.

### Alternate flows

#### AF-01 — Choose what to remove in a recurring income
Triggered at step 1 when the income is recurring.

1. The system asks whether to remove a single month or the whole income,
   with the current accounting month preselected, and reminds the user that
   the income can be stopped instead (FR-06).
2. The user chooses the scope and, for a single month, optionally another
   month; the flow continues at step 2, and the warning states what will be
   removed.
3. If the user chooses to stop the income instead, the flow continues in
   UC-22; if the user cancels, nothing is removed.

### Exception flows

#### EF-01 — Income not found
Triggered at step 4 when no income on an account the user owns has that id,
or it has already been removed.

1. The system removes nothing and displays "Income not found".

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system removes nothing and directs the user to authenticate.

#### EF-03 — Invalid scope or month
Triggered at step 4 when `scope` or `month` is missing, malformed or not
allowed for a recurring income (FR-06), or is supplied for a one-off income.

1. The system removes nothing, names the offending parameter, and the flow
   returns to step 1.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-07).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Removing a `5000.00` income of the current month from an account
  with balance `5200.00` makes the balance `200.00`.
- AC-02 — With August consolidated, removing an August income corrects the
  August closing and leaves the current balance unchanged; the warning names
  August.
- AC-03 — A removed income appears in no list, detail or month view.
- AC-04 — Removing it again produces EF-01.
- AC-05 — Removing only December from a recurring salary means nothing is
  received on 5 December, and January is received normally.
- AC-06 — Removing a recurring income asks whether to remove a single month
  or the whole income, and mentions stopping it instead.

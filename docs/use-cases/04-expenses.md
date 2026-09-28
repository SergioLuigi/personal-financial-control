# Expenses — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-15 to UC-23 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-15 — Register a credit card purchase](#uc-15--register-a-credit-card-purchase)
- [UC-16 — Register a Pix expense](#uc-16--register-a-pix-expense)
- [UC-17 — Schedule a Pix](#uc-17--schedule-a-pix)
- [UC-18 — View expense details](#uc-18--view-expense-details)
- [UC-19 — Update an expense](#uc-19--update-an-expense)
- [UC-20 — List expenses](#uc-20--list-expenses)
- [UC-21 — Remove an expense](#uc-21--remove-an-expense)
- [UC-22 — Stop a recurring expense or income](#uc-22--stop-a-recurring-expense-or-income)
- [UC-23 — Resume a recurring expense or income](#uc-23--resume-a-recurring-expense-or-income)


## UC-15 — Register a credit card purchase

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-06, UC-07, UC-16, UC-18

### Objective

Allow an authenticated user to register a purchase made with one of their own
credit cards — a one-off purchase, a purchase in installments, or a recurring
charge — so that the card's balance reflects what has been spent.

### Actors

- **User** (primary) — the owner of the card.
- **System** (secondary) — validates the expense, enforces ownership of the
  card, persists the expense and recalculates the card's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns at least one credit card.

### Post-conditions

- POST-01 — On success, an expense is persisted, associated with the chosen
  credit card, and the card's balance reflects it (UC-06 FR-09).
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field            | Type          | Required | Constraints                                             |
|------------------|---------------|----------|---------------------------------------------------------|
| `credit_card_id` | text          | Yes      | Must match a credit card the user owns (UC-07)          |
| `description`    | text          | Yes      | 1–100 chars, trimmed; what was bought                   |
| `category_id`    | text          | Yes      | One of the user's expense categories (UC-12)       |
| `date`           | date          | Yes      | Date of the purchase or first charge; not in the future |
| `value`          | decimal(13,2) | Yes      | Greater than `0.00`; the total value of the purchase    |
| `recurring`      | boolean       | No       | Defaults to `false`                                     |
| `installments`   | integer       | No       | ≥ 1, defaults to `1`; must be `1` when `recurring`      |
| `accounting_month` | text        | No       | `YYYY-MM`; month of the first charge; defaults per the Glossary |

### Functional requirements

- FR-01 — The system persists a new expense associated with exactly one
  credit card owned by the requesting user. A card may have any number of
  expenses.
- FR-02 — An expense has a description, a category, a date, a value, a
  recurring flag and a number of installments. The description and the
  category are mandatory for every expense, whatever its kind, source or
  payment method.
- FR-03 — Every expense is one of three kinds:
  - **one-off** — not recurring, 1 installment;
  - **in installments** — not recurring, 2 or more installments;
  - **recurring** — charged again every month, always 1 installment.
  An expense cannot be both recurring and in installments.
- FR-04 — For an expense in installments, `value` is the total of the
  purchase. It is divided into equal installments rounded down to the cent,
  and any remaining cents are added to the first installment
  (`100.00` in 3 → `33.34`, `33.33`, `33.33`).
- FR-05 — Installment *k* is dated *k − 1* months after `date`. A recurring
  expense is charged on the same day every month from `date` onwards. In a
  month shorter than that day, the date falls on the month's last day (as in
  UC-06 FR-05).
- FR-06 — For the card's balance (UC-06 FR-09), the total value of expenses
  counts:
  - the full `value` of a one-off expense or an expense in installments, from
    its `date` — the whole purchase uses the limit at once, not one
    installment at a time;
  - `value` once for every monthly charge of a recurring expense whose date
    has been reached, except removed months (UC-21) and months in which the
    expense was stopped (UC-22, UC-23).
  Removed expenses (UC-21) never count. The states follow the Glossary.
  Changes to charges that belong to a consolidated accounting month correct
  that month's closing and never the current balance (Glossary, "Changes to
  the past").
- FR-07 — An expense that makes the card's balance negative is accepted
  (UC-06 FR-11).
- FR-08 — Registering an expense is started from the card's details (UC-07),
  with that card preselected.
- FR-09 — Any `credit_card_id` that does not match a card the user owns
  produces the same "not found" response as UC-07 FR-02.
- FR-10 — Every expense has a funding source — a credit card or a bank
  account — and a payment method — `card` or `pix`. Expenses registered here
  are funded by a credit card with payment method `card`. Pix expenses
  (UC-16) may be funded by either source.
- FR-11 — The kinds, installment rules and dates in FR-03 to FR-05 apply to
  every expense, whatever its source and payment method, except where a use
  case restricts them (UC-16 FR-03).
- FR-12 — Every charge belongs to an accounting month, stored with it
  (Glossary, "Accounting month of a charge"). The user chooses the accounting
  month of the first charge; it defaults to the current accounting month, or
  to the next open one if the current one is consolidated. Installment *k*
  and the *k*-th recurring charge belong to the *k − 1*-th month after it.
- FR-13 — The accounting month may differ from the month of the date, and
  may be any month: open, consolidated or future, subject to the pending
  month lock (Glossary). Choosing a consolidated month shows the past
  expense warning (UC-19 FR-12) before the expense is registered. As a new
  entry, the expense changes the card's current balance normally and is
  recorded as a correction in that month's closing (Glossary, "Changes to
  the past"; UC-36 FR-06).
- FR-14 — The chosen accounting month must exist; when it does not, the user
  is required to create it first (UC-33). The months of later
  installments or recurring charges do not need to exist yet (Glossary).
- FR-15 — The category is chosen from the user's categories (UC-12),
  which include the default list (UC-11 FR-05).
- FR-16 — When a recurring expense starts in an accounting month earlier than
  the current one, the system asks whether to include the past months:
  - **yes** — a charge is counted for every month from the start, as the
    rules above describe;
  - **no** — the expense starts in the current accounting month: its first
    charge is dated on the same day in the current month, and no earlier
    month is charged.
  The question is not asked for a start in the current month.
- FR-17 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a credit card's details (UC-07), the user chooses to add an expense.
2. The system presents an empty expense form with that card preselected,
   `recurring` off and `installments` set to 1.
3. The user supplies a description, a category, a date and a value, and
   optionally marks the expense as
   recurring, sets a number of installments, or chooses another accounting
   month (FR-12, FR-13).
   *(AF-01 if the chosen month does not exist.)*
4. The user confirms the registration.
   *(AF-03 if the chosen accounting month is consolidated.)*
5. The system retrieves the card, restricted to cards the user owns.
   *(EF-01 if no such card is found.)*
6. The system validates the submitted data.
   *(EF-02 if the date is invalid; EF-03 if the value is invalid; EF-04 if the
   number of installments is invalid; EF-05 if the expense is both recurring
   and in installments; EF-07 if the description is invalid; EF-08 if the
   category is invalid.)*
   *(AF-02 if the expense is recurring and starts in an earlier month.)*
7. The system persists the expense, recalculates the card's balance (FR-06)
   and confirms: "Expense registered".
8. The user returns to the card's details (UC-07), which show the new
   balance.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing.
2. Once it is created, the flow continues at step 3 with that month chosen;
   if the user does not create it, nothing is registered.

#### AF-02 — Recurring expense starting in the past
Triggered at step 6 when the expense is recurring and its first charge falls
in an accounting month earlier than the current one.

1. The system asks whether to include the past months (FR-16), stating how
   many months and how much they add up to.
2. The user chooses, and the flow continues at step 7 accordingly.
3. If the user cancels, nothing is registered.

#### AF-03 — Consolidated month
Triggered at step 4 when the chosen accounting month is consolidated.

1. The system shows the past entry warning (UC-19 FR-12), naming the month:
   as a new entry, it changes the current balance and is recorded as a
   correction in that month's closing (Glossary, "Changes to the past").
2. If the user acknowledges, the flow continues; if not, it returns to
   step 3 and nothing is registered.

### Exception flows

#### EF-01 — Credit card not found
Triggered at step 5 when no card the user owns has that id — because no card
has it, the card has been deleted, or another user owns it.

1. The system does not persist the expense.
2. The system displays "Credit card not found" and offers a way back to the
   credit cards list, and the flow ends.

#### EF-02 — Invalid date
Triggered at step 6 when the date is missing, not a valid date, or later than
today.

1. The system does not persist the expense.
2. The system displays the corresponding message next to the date field
   ("Date is required" / "Date cannot be in the future"), and the flow
   returns to step 3.

#### EF-03 — Invalid value
Triggered at step 6 when the value is missing, not a valid amount with at
most two decimal places, or not greater than `0.00`.

1. The system does not persist the expense.
2. The system displays "Value must be a positive amount" next to the value
   field, and the flow returns to step 3.

#### EF-04 — Invalid installments
Triggered at step 6 when the number of installments is not an integer, or
is lower than 1.

1. The system does not persist the expense.
2. The system displays "Installments must be a whole number of at least 1"
   next to the installments field, and the flow returns to step 3.

#### EF-05 — Recurring expense in installments
Triggered at step 6 when the expense is recurring and has more than 1
installment.

1. The system does not persist the expense.
2. The system displays "A recurring expense cannot have installments", and
   the flow returns to step 3.

#### EF-06 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

#### EF-07 — Invalid description
Triggered at step 6 when the description is empty after trimming, or longer
than 100 characters.

1. The system does not persist the expense.
2. The system displays the corresponding message next to the description
   field, and the flow returns to step 3.

#### EF-08 — Invalid category
Triggered at step 6 when no category is chosen, or the category is not one
of the user's expense categories.

1. The system does not persist the expense.
2. The system displays "Choose a category" next to the category field, and
   the flow returns to step 3.

#### EF-09 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-17).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Registering a one-off expense of `200.00` on a card with balance
  `5000.00` succeeds and the balance becomes `4800.00`.
- AC-02 — An expense with only a description, a category, a date and a value
  is stored as not recurring, with 1 installment.
- AC-03 — An expense of `1200.00` in 12 installments reduces the balance by
  `1200.00` at once, and has 12 installments of `100.00` dated one month
  apart.
- AC-04 — An expense of `100.00` in 3 installments has installments of
  `33.34`, `33.33` and `33.33`.
- AC-05 — A recurring expense of `50.00` dated 15 January reduces the balance
  by `50.00` on registration, and by `50.00` more on 15 February, 15 March and
  every month after.
- AC-06 — A recurring expense or an expense in installments dated the 31st
  falls on 30 April and on the last day of February.
- AC-07 — `recurring=true` with `installments=3` fails with EF-05 and nothing
  is persisted.
- AC-08 — Values `0.00`, `-10.00` and `10.001` each fail with EF-03.
- AC-09 — Installments `0`, `-3` and `2.5` each fail with EF-04; `1` and `60`
  succeed.
- AC-10 — A date later than today fails with EF-02; today's date succeeds.
- AC-11 — An expense that takes the balance from `100.00` to `-50.00` is
  accepted.
- AC-12 — Using another user's card, a non-existent id, or an arbitrary
  string such as `abc` as `credit_card_id` produces the identical EF-01
  response, and nothing is persisted.
- AC-13 — An unauthenticated request persists nothing.
- AC-14 — An expense registered without an accounting month belongs to the
  current one; with closing day 25, one dated 27 September belongs to
  `2026-10`.
- AC-15 — A purchase dated 29 September assigned to `2026-10` in 3
  installments has charges in October, November and December.
- AC-16 — Assigning a new expense to consolidated August shows the past
  expense warning; once confirmed, it lowers the card's current available
  limit and is recorded as a correction in the August closing.
- AC-17 — Choosing an accounting month that was never created requires the
  user to create it before the expense can be registered; an expense in 12
  installments can be registered while only its first month exists.
- AC-18 — An expense without a description or without a category is rejected
  with EF-07 or EF-08.
- AC-19 — A new user can register an expense in "Groceries" without creating
  any category.
- AC-20 — On 27 September, registering a `39.90` recurring subscription
  starting 15 January asks whether to include the past months: choosing yes
  counts January to September; choosing no makes its first charge
  15 September.


## UC-16 — Register a Pix expense

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-07, UC-15, UC-17, UC-18, UC-19, UC-20

### Objective

Allow an authenticated user to register an expense paid by Pix, funded either
by the balance of one of their own bank accounts or by the limit of one of
their own credit cards, so that the funding source's balance reflects what has
been spent.

### Actors

- **User** (primary) — the owner of the funding source.
- **System** (secondary) — validates the expense, enforces ownership of the
  funding source, persists the expense and updates the source's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns at least one bank account or credit card.

### Post-conditions

- POST-01 — On success, a Pix expense is persisted, associated with the
  chosen funding source, and the source's balance reflects it.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field            | Type          | Required | Constraints                                                   |
|------------------|---------------|----------|---------------------------------------------------------------|
| `source_type`    | text          | Yes      | `bank_account` or `credit_card`                               |
| `source_id`      | text          | Yes      | Must match a bank account (UC-03) or card (UC-07) the user owns |
| `recipient_name` | text          | Unless a transfer | 1–100 chars, trimmed; for an internal transfer, the destination account's name |
| `destination_account_id` | text  | No       | One of the user's other bank accounts; makes the Pix an internal transfer |
| `description`    | text          | Yes      | 1–100 chars, trimmed                                          |
| `category_id`    | text          | Unless a transfer | One of the user's expense categories (UC-12)      |
| `date`           | date          | Yes      | Date of the Pix or of its first charge; not in the future     |
| `value`          | decimal(13,2) | Yes      | Greater than `0.00`; the total value of the Pix               |
| `recurring`      | boolean       | No       | Defaults to `false`                                           |
| `installments`   | integer       | No       | ≥ 1, defaults to `1`; must be `1` when `recurring` or when the source is a bank account |
| `accounting_month` | text        | No       | `YYYY-MM`; month of the first charge; defaults per the Glossary |

### Functional requirements

- FR-01 — The system persists a new Pix expense funded by exactly one source
  owned by the requesting user: a bank account or a credit card.
- FR-02 — A Pix expense has a funding source, a recipient name, a
  description, a category, a date, a value, a recurring flag and a number of
  installments.
- FR-03 — A Pix expense may be one-off or recurring with either source. It may
  be in installments only when funded by a credit card. The kinds follow
  UC-15 FR-03, the installment and recurring dates follow UC-15 FR-04 and
  FR-05, and the accounting months of its charges follow UC-15 FR-12 and
  FR-13.
- FR-04 — Funded by a bank account, the Pix reduces the account's balance:
  - a one-off Pix by its `value`, on registration;
  - a recurring Pix by its `value` once for every monthly charge whose date
    has been reached, except removed months (UC-21) and months in which the
    Pix was stopped (UC-22, UC-23).
  A removed Pix (UC-21) never counts. The states follow the Glossary.
  Changes to charges that belong to a consolidated accounting month correct
  that month's closing and never the current balance (Glossary, "Changes to
  the past").
- FR-05 — A Pix funded by a bank account is accepted even when it makes the
  account's balance negative.
- FR-06 — Funded by a credit card, the Pix uses the card's limit and counts
  towards the card's balance exactly like a card purchase (UC-15 FR-06,
  FR-07), so it may make the card's balance negative (UC-06 FR-11).
- FR-07 — The value is final: the system adds no fee or interest (Glossary,
  "Recording only"). If the bank charged a fee, the user includes it in the
  value or registers it as a separate expense.
- FR-08 — Registering a Pix is started from the funding source's details —
  a bank account's details (UC-03) or a credit card's details (UC-07) — with
  that source preselected.
- FR-09 — Any `source_id` that does not match a source of that type owned by
  the user produces the same "not found" response as UC-03 FR-02 or UC-07
  FR-02.
- FR-10 — A Pix is an expense with payment method `pix` (UC-15 FR-10),
  whatever its source. Every Pix, including those paid from a bank account,
  can be viewed (UC-18), updated (UC-19) and found in the expenses list
  (UC-20), where it can be searched by payment method, recipient and source.
- FR-11 — The chosen accounting month must exist; when it does not, the user
  is required to create it first (UC-33).
- FR-12 — A Pix with a future date is scheduled instead (UC-17).
- FR-13 — A Pix has a mandatory description and category, as every expense
  (UC-15 FR-02, FR-15). For a recurring Pix starting in an earlier month, the
  system asks whether to include the past months (UC-15 FR-16).
- FR-14 — When the recipient is one of the user's own bank accounts, the Pix
  is an internal transfer (Glossary):
  - it must be funded by a bank account other than the destination;
  - the destination account's balance rises by the same value, on the same
    date as the source's falls, in the same accounting month;
  - it is not counted in any month's total spent or received, and its
    category is the fixed "Internal transfer", which the user does not
    choose;
  - changing or removing it (UC-19, UC-21) changes or removes both sides.
  Recurring and scheduled internal transfers follow the same rules on each
  date.
- FR-15 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a bank account's details (UC-03) or a credit card's details (UC-07), the
   user chooses to register a Pix.
2. The system presents an empty Pix form with that source preselected,
   `recurring` off and `installments` set to 1. The installments field is
   offered only when the source is a credit card.
3. The user supplies the recipient — a name, or one of their own bank
   accounts for an internal transfer (FR-14) — a description, a category
   (except for a transfer), a date and a value, and optionally
   marks the Pix as recurring, sets a number of installments on a credit
   card, or chooses another accounting month.
   *(AF-01 if the chosen month does not exist.)*
4. The user confirms the registration.
   *(AF-03 if the chosen accounting month is consolidated.)*
5. The system retrieves the funding source, restricted to sources the user
   owns.
   *(EF-01 if no such source is found.)*
6. The system validates the submitted data.
   *(EF-02 if the recipient name is invalid; EF-03 if the date is invalid;
   EF-04 if the value is invalid; EF-05 if the number of installments is
   invalid; EF-06 if the Pix is both recurring and in installments; EF-07 if
   installments are set on a bank account; EF-09 if the description or
   category is invalid; EF-10 if the transfer destination is invalid.)*
   *(AF-02 if the Pix is recurring and starts in an earlier month.)*
7. The system persists the Pix expense, updates the source's balance (FR-04
   or FR-06) — and, for an internal transfer, the destination's — and
   confirms: "Pix to <recipient name> registered".
8. The user returns to the source's details, which show the new balance.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing.
2. Once it is created, the flow continues at step 3 with that month chosen;
   if the user does not create it, nothing is registered.

#### AF-02 — Recurring Pix starting in the past
Triggered at step 6 when the Pix is recurring and its first charge falls in an
accounting month earlier than the current one.

1. The system asks whether to include the past months (UC-15 FR-16).
2. The user chooses, and the flow continues at step 7 accordingly.

#### AF-03 — Consolidated month
Triggered at step 4 when the chosen accounting month is consolidated.

1. The system shows the past entry warning (UC-19 FR-12), naming the month:
   as a new entry, it changes the current balance and is recorded as a
   correction in that month's closing (Glossary, "Changes to the past").
2. If the user acknowledges, the flow continues; if not, it returns to
   step 3 and nothing is registered.

### Exception flows

#### EF-01 — Funding source not found
Triggered at step 5 when no source of that type owned by the user has that
id — because none has it, it has been deleted, or another user owns it.

1. The system does not persist the Pix.
2. The system displays "Bank account not found" or "Credit card not found",
   offers a way back to the corresponding list, and the flow ends.

#### EF-02 — Invalid recipient name
Triggered at step 6 when the recipient name is empty after trimming, or
longer than 100 characters.

1. The system does not persist the Pix.
2. The system displays the corresponding message next to the recipient field
   ("Recipient name is required" / "Recipient name must be at most 100
   characters"), and the flow returns to step 3.

#### EF-03 — Invalid date
Triggered at step 6 when the date is missing, not a valid date, or later than
today.

1. The system does not persist the Pix.
2. The system displays the corresponding message next to the date field
   ("Date is required" / "Date cannot be in the future"), and the flow
   returns to step 3.

#### EF-04 — Invalid value
Triggered at step 6 when the value is missing, not a valid amount with at
most two decimal places, or not greater than `0.00`.

1. The system does not persist the Pix.
2. The system displays "Value must be a positive amount" next to the value
   field, and the flow returns to step 3.

#### EF-05 — Invalid installments
Triggered at step 6 when the number of installments is not an integer, or is
lower than 1.

1. The system does not persist the Pix.
2. The system displays "Installments must be a whole number of at least 1"
   next to the installments field, and the flow returns to step 3.

#### EF-06 — Recurring Pix in installments
Triggered at step 6 when the Pix is recurring and has more than 1
installment.

1. The system does not persist the Pix.
2. The system displays "A recurring Pix cannot have installments", and the
   flow returns to step 3.

#### EF-07 — Installments on a bank account
Triggered at step 6 when the source is a bank account and the number of
installments is greater than 1.

1. The system does not persist the Pix.
2. The system displays "Installments are only available for Pix on a credit
   card", and the flow returns to step 3.

#### EF-08 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

#### EF-09 — Invalid description or category
Triggered at step 6 when the description is missing or longer than 100
characters, or — except for an internal transfer — no valid category is
chosen.

1. The system does not persist the Pix.
2. The system displays the corresponding message next to the field, and the
   flow returns to step 3.

#### EF-10 — Invalid transfer destination
Triggered at step 6 when `destination_account_id` does not match another of
the user's bank accounts, is the source account itself, or the source is a
credit card.

1. The system does not persist the Pix.
2. The system displays "Choose another of your bank accounts" next to the
   recipient field, and the flow returns to step 3.

#### EF-11 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-15).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — A one-off Pix of `200.00` to "Mary Smith" from a bank account with
  balance `1000.00` succeeds, and the account's balance becomes `800.00`.
- AC-02 — A one-off Pix of `300.00` from a bank account with balance `100.00`
  succeeds, and the balance becomes `-200.00`.
- AC-03 — A recurring Pix of `50.00` dated 15 January from a bank account
  reduces its balance by `50.00` on registration, and by `50.00` more on
  15 February, 15 March and every month after.
- AC-04 — A Pix of `1200.00` in 12 installments on a card with balance
  `5000.00` succeeds; the card's balance becomes `3800.00`, and the expense
  appears among the card's expenses (UC-07) with payment method `pix` and
  recipient "Mary Smith".
- AC-05 — A recurring Pix of `50.00` on a card counts towards the card's
  balance like a recurring card expense (UC-15 AC-05).
- AC-06 — A `1000.00` Pix on a card is recorded as `1000.00`; no fee or
  interest is ever added.
- AC-07 — A Pix from a bank account with `installments=3` fails with EF-07,
  and nothing is persisted.
- AC-08 — `recurring=true` with `installments=3` on a card fails with EF-06.
- AC-09 — An empty recipient name, or one of 101 characters, fails with
  EF-02.
- AC-10 — Values `0.00`, `-10.00` and `10.001` each fail with EF-04; a date
  later than today fails with EF-03.
- AC-11 — Using another user's bank account or card, a non-existent id, or an
  arbitrary string such as `abc` as `source_id` produces the identical EF-01
  response, and nothing is persisted.
- AC-12 — An unauthenticated request persists nothing.
- AC-13 — A Pix paid from a bank account appears in the expenses list (UC-20)
  with `payment_method=pix&source_type=bank_account`, and can be opened
  (UC-18).
- AC-14 — Choosing an accounting month that was never created requires the
  user to create it before the Pix can be registered.
- AC-15 — A `1000.00` Pix from account A to the user's account B lowers A by
  `1000.00` and raises B by `1000.00`; the month's total spent and total
  received do not change.
- AC-16 — Removing that transfer restores both balances.
- AC-17 — A Pix to the source account itself, or from a credit card to one of
  the user's accounts, fails with EF-10.
- AC-18 — A Pix without a description, or a non-transfer Pix without a
  category, fails with EF-09.


## UC-17 — Schedule a Pix

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-07, UC-16, UC-18, UC-19, UC-21, UC-38, Glossary

### Objective

Allow an authenticated user to register a Pix that will be made on a future
date, funded by a bank account's balance or a credit card's limit, so that it
appears in the forecast and counts towards the balance only when its date
arrives.

### Actors

- **User** (primary) — the owner of the funding source.
- **System** (secondary) — validates and stores the scheduled Pix, and makes
  it count on its date.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns at least one bank account or credit card.

### Post-conditions

- POST-01 — On success, a Pix is stored as scheduled (Glossary). No balance
  changes until its date.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

The same fields as UC-16, except `date`:

| Field  | Type | Required | Constraints                             |
|--------|------|----------|-----------------------------------------|
| `date` | date | Yes      | Later than today; the date of the Pix   |

### Functional requirements

- FR-01 — A scheduled Pix follows every rule of UC-16 — funding source,
  recipient, value, recurrence, installments on a credit card, accounting
  month — except that its date is in the future.
- FR-02 — Until its date, the Pix is scheduled: it appears in its source's
  details, in the expenses list and in the forecast of its accounting month,
  marked as scheduled, and does not count towards any balance.
- FR-03 — On its date, the Pix counts towards its funding source's balance as
  in UC-16 FR-04 or FR-06, and becomes active. A recurring scheduled Pix is
  charged from that date on, every month.
- FR-04 — Before its date, a scheduled Pix can be changed (UC-19), including
  its date, which must stay in the future, or cancelled by removing it
  (UC-21); cancelling changes no balance.
- FR-05 — The accounting month defaults to the month whose period contains
  the scheduled date; the user may choose another. It must exist, or the
  user is required to create it first (UC-33).
- FR-06 — Scheduling is started from a bank account's details (UC-03) or a
  credit card's details (UC-07), with that source preselected.
- FR-07 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a bank account's or credit card's details, the user chooses to schedule
   a Pix.
2. The system presents the Pix form (UC-16) with the source preselected and
   a date field that accepts only future dates.
3. The user supplies the recipient — a name or one of their own bank
   accounts — a description, a category (except for a transfer), the future
   date and the value, and optionally the recurrence, installments on a
   credit card, or another accounting month.
   *(AF-01 if the chosen month does not exist.)*
4. The user confirms.
5. The system validates the data as in UC-16, and that the date is in the
   future.
   *(EF-01 if the date is not in the future; the exceptions of UC-16
   otherwise.)*
6. The system stores the Pix as scheduled and confirms: "Pix to <recipient>
   scheduled for <date>".
7. The user returns to the source's details, where the Pix appears as
   scheduled.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing.
2. Once it is created, the flow continues at step 3; if the user does not
   create it, nothing is scheduled.

### Exception flows

#### EF-01 — Date not in the future
Triggered at step 5 when the date is today or earlier.

1. The system does not persist the Pix.
2. The system displays "A scheduled Pix must have a future date; to register
   a Pix already made, use Register a Pix", and the flow returns to step 3.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

#### EF-03 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-07).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — On 27 September, scheduling a `200.00` Pix from a bank account for
  3 October leaves the account's balance unchanged; on 3 October the balance
  drops by `200.00` and the Pix becomes active.
- AC-02 — Until 3 October, the Pix appears as scheduled in the account's
  details, in the expenses list with `status=scheduled`, and in the forecast
  of its accounting month.
- AC-03 — Removing the scheduled Pix before 3 October cancels it and no
  balance ever changes.
- AC-04 — Changing its date to 10 October succeeds; changing it to today
  fails.
- AC-05 — A scheduled Pix on a credit card in 3 installments counts on the
  card's limit only from its date.
- AC-06 — Scheduling for today or an earlier date fails with EF-01.
- AC-07 — An unauthenticated request persists nothing.


## UC-18 — View expense details

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-07, UC-15, UC-16, UC-19, UC-20, UC-21, UC-22, UC-23

### Objective

Allow an authenticated user to see the full details of one of their own
expenses — whether funded by a bank account or a credit card, and whether
paid by card or by Pix — including when each installment or recurring charge
falls.

### Actors

- **User** (primary) — the owner of the funding source and of the expense.
- **System** (secondary) — retrieves the expense, calculates its schedule and
  enforces ownership.

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

| Field              | Type                 | Notes                                                                  |
|--------------------|----------------------|------------------------------------------------------------------------|
| `id`               | text                 | The expense's id                                                       |
| `source`           | object               | `type` (`bank_account` or `credit_card`), `id` and `name` of the funding source |
| `description`      | text                 | As stored                                                              |
| `category`         | object               | `id` and `name`                                                        |
| `refund`           | boolean              | Whether the entry is a card refund (UC-32); its value lowers the bill |
| `destination_account` | object            | Internal transfers only: `id` and `name` of the destination account    |
| `date`             | date                 | As stored                                                              |
| `value`            | decimal(13,2)        | As stored; the total of the purchase                                   |
| `recurring`        | boolean              | As stored                                                              |
| `installments`     | integer              | As stored                                                              |
| `payment_method`   | text                 | `card` or `pix` (UC-15 FR-10)                                          |
| `recipient_name`   | text                 | Pix only: the recipient (UC-16)                                        |
| `accounting_month` | text                 | Accounting month of the first charge (UC-15 FR-12)                     |
| `schedule`         | array of installment | `number`, `date`, `amount`, `accounting_month` of each installment (UC-15 FR-04, FR-05, FR-12); one entry for a one-off expense; empty for a recurring one |
| `next_charge_date` | date                 | Recurring expenses only: date of the next monthly charge (UC-15 FR-05); empty while stopped |
| `status`           | text                 | `scheduled`, `active` or `stopped` (Glossary); only a recurring expense can be `stopped` |
| `months`           | array of month       | Recurring expenses only: `accounting_month`, `date`, `amount` and `situation` (`charged`, `removed`, `stopped` or `forecast`) of each month, from the first charge to the next forecast one |
| `stop_periods`     | array of period      | Recurring expenses only: `stopped_on` and `resumed_on` (empty while still stopped) of each stop (UC-22, UC-23) |

### Functional requirements

- FR-01 — The system returns an expense only to the user who owns its
  funding source.
- FR-02 — Any id that does not match an expense funded by a source the user
  owns produces the same "not found" response, whether no expense has that id
  or it belongs to another user. The user cannot learn whether another user's
  expense exists.
- FR-03 — The schedule and the next charge date are calculated at the moment
  of the request from the stored date, value, recurring flag and number of
  installments.
- FR-04 — The details are reached from its funding source's details — a bank
  account's (UC-03) or a credit card's (UC-07) — or the expenses list
  (UC-20), among other places such as a bill (UC-29), an accounting month
  (UC-37) or the home summary (UC-38).
- FR-05 — From the details, the user can return to where they came from: the
  bank account's or card's details on the same page of expenses, or the
  expenses list (UC-20) on the same page and with the same filters they
  left.
- FR-06 — A removed expense (UC-21) produces the same "not found" response as
  FR-02. Removed months do not appear in the schedule and are never the next
  charge date.
- FR-07 — A stopped expense is shown, marked as stopped, with its stop
  periods. Months in which it was stopped have no charge.
- FR-08 — A recurring expense shows its month history: every accounting month
  from its first charge to its next forecast charge, each marked as charged,
  removed (UC-21), stopped (UC-22) or forecast, with the amount charged in it
  (UC-19 FR-15). This is where the user picks a month to remove.
- FR-09 — A scheduled Pix (UC-17) is shown, marked as scheduled, with the
  date on which it will count.

### Main flow

1. On a bank account's details (UC-03), a credit card's details (UC-07) or
   the expenses list (UC-20), the user selects an expense.
2. The system retrieves the expense with that id, restricted to expenses
   funded by sources the user owns.
   *(EF-01 if no such expense is found.)*
3. The system calculates the schedule, or the next charge date for a
   recurring expense (FR-03).
4. The system displays the expense's funding source, payment method,
   recipient (Pix only), date, value, recurring flag, number of installments,
   and its schedule or next charge date.
5. The user returns to where they came from (FR-05).
6. Alternatively, the user may choose to edit the expense, leading to UC-19;
   to remove it, leading to UC-21; or, for a recurring expense, to stop it,
   leading to UC-22, or to resume it when stopped, leading to UC-23.

### Exception flows

#### EF-01 — Expense not found
Triggered at step 2 when no expense funded by a source the user owns has
that id — because no expense has it, the expense has been removed, or it
belongs to another user.

1. The system returns no expense data.
2. The system displays "Expense not found" and offers a way back to the
   expenses list, and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no expense data whatsoever and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — Opening one of the user's own expenses shows its funding source,
  payment method, date, value, recurring flag and number of installments
  exactly as stored.
- AC-02 — An expense of `100.00` in 3 installments dated 10 January shows a
  schedule of `33.34` on 10 January, `33.33` on 10 February and `33.33` on
  10 March.
- AC-03 — A one-off expense shows a schedule with a single entry for its
  full value on its date.
- AC-04 — A recurring expense dated 31 January, viewed on 5 April, shows no
  schedule and a next charge date of 30 April.
- AC-05 — Requesting another user's expense, a non-existent id, and an
  arbitrary string such as `abc` all produce the identical EF-01 response,
  never a system error.
- AC-06 — An unauthenticated request returns no expense data.
- AC-07 — A Pix of `200.00` to "Mary Smith" paid from a bank account shows
  the bank account as its source, payment method `pix` and recipient
  "Mary Smith".
- AC-08 — A recurring Pix from January, with May removed and July to August
  stopped, viewed in September, shows January to April and June charged, May
  removed, July and August stopped, September charged and October forecast.
- AC-09 — After a price change from October (UC-19 FR-15), the history shows
  the old amount up to September and the new amount from October.


## UC-19 — Update an expense

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-15, UC-16, UC-18

### Objective

Allow an authenticated user to correct the date, value, recurring flag,
number of installments and, for a Pix, the recipient of one of their own
expenses through a partial update that is idempotent: sending the same
request once or many times leaves the expense in the same state.

### Actors

- **User** (primary) — the owner of the funding source and of the expense.
- **System** (secondary) — validates the changes, enforces ownership,
  persists the expense and recalculates the funding source's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the supplied fields hold the new values, every other
  field is unchanged, and the funding source's balance reflects the updated
  expense.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

#### Request

| Parameter        | Type          | Required | Constraints                                          |
|------------------|---------------|----------|------------------------------------------------------|
| `id`             | text          | Yes      | Any string; identifies the expense (as in UC-18)     |
| `date`           | date          | No       | Not in the future                                    |
| `value`          | decimal(13,2) | No       | Greater than `0.00`; the total value of the purchase |
| `recurring`      | boolean       | No       |                                                      |
| `installments`   | integer       | No       | ≥ 1; greater than 1 only for a credit card source    |
| `recipient_name` | text          | No       | Pix only; 1–100 chars, trimmed                       |
| `description`    | text          | No       | 1–100 chars, trimmed                                 |
| `category_id`    | text          | No       | An expense category; not for a transfer              |
| `accounting_month` | text        | No       | `YYYY-MM`; accounting month of the first charge      |
| `value_scope`    | text          | With `value` on a recurring expense | `all_months` or `from_month` |
| `value_from_month` | text        | With `from_month` | `YYYY-MM`; first month with the new value |

Only `date`, `value`, `recurring`, `installments`, `accounting_month`,
`value_scope`, `value_from_month`, `description`, `category_id` and, for a
Pix, `recipient_name` may be supplied. The destination of an internal
transfer cannot be changed. `id`, the funding source and the payment
method cannot be changed through this use case.

#### Response

The updated expense, with the same fields as UC-18.

### Functional requirements

- FR-01 — The system updates an expense only for the user who owns its
  funding source.
- FR-02 — The update is partial: a field left out of the request keeps its
  current value.
- FR-03 — Every supplied field carries the absolute new value, never a
  relative change. Repeating the same request therefore produces the same
  expense and the same response as sending it once.
- FR-04 — The updated expense, combining the supplied fields with the current
  values of the others, must still be one of the three kinds in UC-15 FR-03:
  it cannot be both recurring and in installments.
- FR-05 — A request that changes nothing — no fields supplied, or values equal
  to the current ones — succeeds and returns the expense unchanged.
- FR-06 — A request containing a field other than `date`, `value`,
  `recurring`, `installments`, `accounting_month` or `recipient_name` — such
  as `source_id`,
  `payment_method` or `id` — is rejected as a whole; nothing is changed. So
  is a request containing `recipient_name` for an expense that is not a Pix.
- FR-07 — Any id that does not match an expense funded by a source the user
  owns produces the same "not found" response as UC-18 FR-02.
- FR-08 — The update is started from the expense's details (UC-18).
- FR-09 — After the update, the expense's schedule is recalculated from the
  updated expense as if it had always had those values. The effect on
  balances is split by accounting month (Glossary, "Changes to the past"):
  - charges in open accounting months change the funding source's current
    balance — a card's per UC-15 FR-06, a bank account's per UC-16 FR-04;
  - charges in consolidated accounting months change only that month's
    closing, never the current balance (UC-36 FR-06).
- FR-10 — When the user changes the value, the system displays a warning
  about the consequences before the update is sent, and the update proceeds
  only if the user acknowledges it. The warning is not shown when the value
  is left unchanged. The warning reads:

  > The value of this expense will change from [A] to [B], a difference of
  > [X].
  > The [balance of account / available limit of card] will change by [Y].
  > *(if in installments)* The [N] installments will be recalculated to [Z]
  > each.
  > *(if on a credit card)* The bills of [months] will change. *(if one was
  > already paid)* Since the [month] bill has already been paid, the
  > difference will go to the next open bill.
  > *(if consolidated months are involved)* The closings of [months] will be
  > corrected.
- FR-11 — An expense funded by a bank account cannot be in installments
  (UC-16 FR-03).
- FR-12 — Past expenses (Glossary) can be changed. When the user changes any
  field of an expense that is past before or after the change — including a
  change of accounting month that moves it into a consolidated month — the
  system displays a warning before the update is sent, and the update
  proceeds only if the user acknowledges it. The warning states that the
  change affects the transaction history and the closing of the consolidated
  accounting months involved, naming them and the bank account or credit
  card, and that the current balance is not changed by those months.
- FR-13 — When both the value warning (FR-10) and the past expense warning
  (FR-12) apply, both are shown together and a single acknowledgement covers
  both.
- FR-14 — Changing `accounting_month` moves every charge of the expense by
  the same number of months, keeping their order (UC-15 FR-12). The new
  month must exist; when it does not, the user is required to create it
  first (UC-33).
- FR-15 — When the value of a recurring expense changes, the user chooses
  how it applies:
  - **all months** — every charge takes the new value, as a correction (FR-09
    and FR-12 apply to every month involved);
  - **from a month on** — charges from the chosen accounting month onwards
    take the new value, and earlier charges keep the value they had. Use it
    for a price change, such as a subscription getting more expensive: past
    months and their closings are not touched.
  The chosen month may be any month in which the expense has, or will have, a
  charge; by default, the current one.
- FR-16 — A scheduled Pix (UC-17) may keep or be given a future date while
  it has not yet counted; any other expense keeps the date rules of UC-15.
- FR-17 — The description and the category can be changed; they cannot be
  cleared, and changing them does not change any balance. An internal
  transfer keeps its fixed category.
- FR-18 — Changing an internal transfer changes both sides (UC-16 FR-14).
- FR-19 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On the expense's details (UC-18), the user chooses to edit the expense.
2. The system presents a form pre-filled with the current description,
   category, date, value, recurring flag, number of installments, accounting
   month and, for a Pix, recipient name. The installments field is offered only when the source is
   a credit card. For a recurring expense, changing the value also asks
   whether it applies to all months or from a month on (FR-15).
3. The user changes any of those fields, including the description and the
   category.
   *(AF-02 if a new accounting month is chosen that does not exist.)*
4. The user confirms the update.
   *(AF-01 if the value was changed or the expense is past.)*
5. The system retrieves the expense with that id, restricted to expenses
   funded by sources the user owns.
   *(EF-01 if no such expense is found.)*
6. The system validates the supplied fields and the resulting kind of
   expense.
   *(EF-02 if an unchangeable field is supplied; EF-03 if the date is invalid;
   EF-04 if the value is invalid; EF-05 if the number of installments is
   invalid; EF-06 if the result is both recurring and in installments; EF-08
   if installments are set on a bank account; EF-09 if the recipient name is
   invalid; EF-11 if the description or category is invalid.)*
7. The system persists the supplied fields, leaving the others untouched,
   recalculates the funding source's balance and the expense's schedule
   (FR-09), and confirms: "Expense updated".
8. The user returns to the expense's details (UC-18), which show the new
   values.

### Alternate flows

#### AF-01 — Value change or past expense warning
Triggered at step 4 when the value differs from the current one, or when the
expense is past before or after the change.

1. Before sending the update, the system displays the value warning (FR-10),
   the past expense warning (FR-12), or both together (FR-13).
2. If the user acknowledges the warning, the flow continues at step 5.
3. If the user cancels, nothing is sent or changed, and the flow returns to
   step 3 with the entered values kept.

#### AF-02 — Accounting month not created
Triggered at step 3 when the new accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing
   (FR-14).
2. Once it is created, the flow continues at step 3; if the user does not
   create it, nothing is sent.

### Exception flows

#### EF-01 — Expense not found
Triggered at step 5 when no expense funded by a source the user owns has that
id.

1. The system changes nothing and returns no expense data.
2. The system displays "Expense not found" and offers a way back to the
   expenses list, and the flow ends.

#### EF-02 — Unchangeable field
Triggered at step 6 when the request contains a field that cannot be changed
(FR-06).

1. The system changes nothing.
2. The system rejects the request, naming the field ("source_id cannot be
   changed"), and the flow ends.

#### EF-03 — Invalid date
Triggered at step 6 when a supplied date is not a valid date, or is later than
today for an expense that is not a scheduled Pix (FR-16).

1. The system changes nothing.
2. The system displays "Date cannot be in the future" next to the date field,
   and the flow returns to step 3.

#### EF-04 — Invalid value
Triggered at step 6 when a supplied value is not a valid amount with at most
two decimal places, or is not greater than `0.00`.

1. The system changes nothing.
2. The system displays "Value must be a positive amount" next to the value
   field, and the flow returns to step 3.

#### EF-05 — Invalid installments
Triggered at step 6 when a supplied number of installments is not an
integer, or is lower than 1.

1. The system changes nothing.
2. The system displays "Installments must be a whole number of at least 1"
   next to the installments field, and the flow returns to step 3.

#### EF-06 — Recurring expense in installments
Triggered at step 6 when the updated expense would be recurring and have more
than 1 installment.

1. The system changes nothing.
2. The system displays "A recurring expense cannot have installments", and
   the flow returns to step 3.

#### EF-07 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing, returns no expense data and directs the user
   to authenticate.

#### EF-08 — Installments on a bank account
Triggered at step 6 when the expense is funded by a bank account and the
supplied number of installments is greater than 1.

1. The system changes nothing.
2. The system displays "Installments are only available for expenses on a
   credit card", and the flow returns to step 3.

#### EF-09 — Invalid recipient name
Triggered at step 6 when a supplied recipient name is empty after trimming,
or longer than 100 characters.

1. The system changes nothing.
2. The system displays the corresponding message next to the recipient field
   ("Recipient name is required" / "Recipient name must be at most 100
   characters"), and the flow returns to step 3.

#### EF-10 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-19).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

#### EF-11 — Invalid description or category
Triggered at step 6 when the description is cleared or longer than 100
characters, or the category is cleared or is not one of the user's expense
categories (FR-17).

1. The system changes nothing.
2. The system displays the corresponding message next to the field, and the
   flow returns to step 3.

### Acceptance criteria

- AC-01 — Sending `date=2026-09-10` changes only the date; every other field
  is unchanged.
- AC-02 — Sending the same request twice succeeds both times, with identical
  responses and an identical stored expense.
- AC-03 — Sending `value=300.00` twice leaves the value at `300.00`, not
  `600.00`.
- AC-04 — Changing the value of a one-off expense dated this month from
  `200.00` to `300.00`, on a card with balance `4800.00`, makes the balance
  `4700.00`.
- AC-05 — Changing an expense of `1200.00` from 12 to 6 installments leaves
  the balance unchanged and makes the schedule 6 installments of `200.00`.
- AC-06 — Changing the value shows the warning; cancelling it leaves the
  expense and the balance unchanged, and acknowledging it applies the update.
- AC-07 — On an expense with no charge in a consolidated accounting month,
  changing only the date, the recurring flag or the installments shows no
  warning, and neither does resending the current value.
- AC-08 — Setting `recurring=true` on an expense with 3 installments, without
  also setting `installments=1`, fails with EF-06 and nothing changes;
  sending `recurring=true&installments=1` succeeds.
- AC-09 — A request containing `source_id` or `payment_method` fails with
  EF-02, even when it also contains a valid `value`, and the value is not
  changed; so does `recipient_name` on a card purchase.
- AC-10 — Values `0.00` and `-10.00`, installments `0` and `2.5`, and a date
  later than today each fail with the matching exception and nothing changes.
- AC-11 — Updating another user's expense, a non-existent id, and an
  arbitrary string such as `abc` all produce the identical EF-01 response,
  and nothing changes.
- AC-12 — An unauthenticated request changes nothing.
- AC-13 — Changing a Pix paid this month from a bank account from `200.00`
  to `250.00` lowers the account's balance by a further `50.00`.
- AC-14 — Setting `installments=3` on a Pix paid from a bank account fails
  with EF-08, and nothing changes.
- AC-15 — Sending `recipient_name=John Smith` on a Pix with no charge in a
  consolidated month changes only the recipient and shows no warning.
- AC-16 — With August consolidated, changing any field of a one-off expense
  of August shows the past expense warning, stating that the transaction
  history and the August closing will change; cancelling leaves it
  unchanged.
- AC-17 — With August consolidated, changing the accounting month of a
  September expense to `2026-08` shows the past expense warning; changing
  only its date to 20 August does not, as its accounting month stays
  September.
- AC-18 — With January consolidated, changing the value of a recurring
  expense started in January for all months shows the value warning and the
  past expense warning together, with a single acknowledgement.
- AC-19 — With August consolidated, changing a Pix of August from `200.00`
  to `250.00` corrects the August closing, and the bank account's current
  balance stays the same. With August still open, the same change lowers the
  current balance by `50.00` and shows no past expense warning.
- AC-20 — With January to August consolidated, changing the value of a
  recurring expense started in January from `50.00` to `60.00` for all months
  corrects the closings of January to August, and changes the current
  balance only by September's `10.00`.
- AC-21 — Changing `accounting_month` of an expense in 3 installments from
  `2026-09` to `2026-10` moves its charges to October, November and
  December.
- AC-22 — In September, changing a `39.90` subscription to `44.90` from
  October keeps `39.90` in every month up to September, shows no past expense
  warning, and charges `44.90` from October on.
- AC-23 — `value_scope` on a non-recurring expense, or `from_month` without
  `value_from_month`, fails with EF-02.
- AC-24 — Changing only the category of an expense of the current month
  changes no balance and shows no warning; clearing the description fails.


## UC-20 — List expenses

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-09, UC-16, UC-18, UC-40

### Objective

Allow an authenticated user to browse all their own expenses — funded by any
of their bank accounts or credit cards, and paid by card or by Pix — ten at a
time, from a dedicated expenses menu, so they can find one to inspect or
edit.

### Actors

- **User** (primary) — the owner of the funding sources and of the
  expenses.
- **System** (secondary) — filters, orders and paginates the expenses.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter        | Type          | Required | Constraints                                  |
|------------------|---------------|----------|----------------------------------------------|
| `page`           | integer       | No       | ≥ 0, defaults to `0` (the first page)        |
| `source_type`    | text          | No       | `bank_account` or `credit_card`              |
| `source_id`      | text          | No       | Requires `source_type`; chosen from the user's own sources of that type |
| `from_date`      | date          | No       | Inclusive lower bound                        |
| `to_date`        | date          | No       | Inclusive upper bound; must be ≥ `from_date` |
| `from_month`     | text          | No       | `YYYY-MM`; inclusive lower bound             |
| `to_month`       | text          | No       | `YYYY-MM`; inclusive upper bound; must be ≥ `from_month` |
| `min_value`      | decimal(13,2) | No       | Inclusive lower bound                        |
| `max_value`      | decimal(13,2) | No       | Inclusive upper bound; must be ≥ `min_value` |
| `recurring`      | boolean       | No       | Omitted means both                           |
| `installments`   | integer       | No       | ≥ 1                                          |
| `payment_method` | text          | No       | `card` or `pix`                              |
| `recipient_name` | text          | No       | 0–100 chars, trimmed                         |
| `description`    | text          | No       | 0–100 chars, trimmed                         |
| `category_id`    | text          | No       | One of the user's categories                 |
| `internal_transfer` | boolean    | No       | Omitted means both                           |
| `refund`         | boolean       | No       | Omitted means both; refunds (UC-32) have negative values |
| `status`         | text          | No       | `scheduled`, `active` or `stopped`; omitted means all |

#### Response

| Field         | Type             | Notes                                                                                   |
|---------------|------------------|-----------------------------------------------------------------------------------------|
| `items`       | array of expense | `id`, `source` (`type`, `id`, `name`), `description`, `category` (`id`, `name`), `internal_transfer`, `refund`, `date`, `accounting_month`, `value`, `recurring`, `installments`, `payment_method`, `recipient_name`, `status`; 0–10 |
| `page`        | integer          | Echoes the page returned                                                                |
| `total_items` | integer          | Expenses matching the filters, across all pages                                         |
| `total_pages` | integer          | `ceil(total_items / 10)`, minimum `1`                                                   |

### Functional requirements

- FR-01 — The page contains only expenses funded by sources owned by the
  requesting user, whether bank accounts or credit cards.
- FR-02 — A page contains at most 10 expenses.
- FR-03 — Expenses are ordered by date descending (most recent first), with
  `id` as the final tie-breaker, so that ordering is stable: no expense
  appears on two pages or is skipped between them.
- FR-04 — Pages are numbered from 0. The system reports `total_pages` so the
  user can move backwards, move forwards, or jump straight to a page number.
- FR-05 — The user may filter by every expense field except `id`:
  - the source type matches exactly, and the source matches the chosen bank
    account or credit card exactly;
  - the date is filtered by a range, inclusive at both ends;
  - the accounting month is filtered by a range of months, inclusive at both
    ends; it coexists with the date filter, and both may be used together;
  - the value, a monetary field, is filtered by a range, inclusive at both
    ends;
  - the recurring flag, the number of installments and the payment method
    match exactly;
  - the recipient name and the description match case-insensitively as
    substrings, after trimming;
  - the category and the internal transfer flag match exactly;
  - the status matches exactly.
  Either bound of a range may be left out.
- FR-06 — The date filter applies to the expense's `date` — the purchase or
  first charge — not to the dates of later installments or recurring charges.
- FR-07 — The value filter applies to the expense's total `value`, not to the
  amount of each installment.
- FR-08 — Filters combine with AND: an expense is returned only when it
  satisfies every filter supplied.
- FR-09 — A `source_id` that does not match a source of that type owned by
  the user returns an empty page, the same as a source with no expenses. It
  is not an error, and the user cannot learn whether another user's bank
  account or card exists.
- FR-10 — `total_items` and `total_pages` reflect the active filters, not the
  user's whole collection.
- FR-11 — A page beyond the last one returns an empty `items` array with
  correct metadata. It is a valid result, not an error.
- FR-12 — A user with no expenses receives an empty page. This is a valid
  result, not an error.
- FR-13 — The list is reached from its own expenses menu, independently of
  the bank accounts and credit cards menus.
- FR-14 — Removed expenses (UC-21) never appear, whatever the filters, and
  never count in `total_items`. Stopped expenses (UC-22) appear, marked as
  stopped, unless excluded by the status filter. Every expense that is not
  stopped or scheduled — including one-off expenses and expenses in
  installments — has status `active`. Scheduled Pix (UC-17) appear, marked
  as scheduled.
- FR-15 — The accounting month filter returns an expense when at least one
  of its charges belongs to a month in the range (UC-15 FR-12), so an
  expense in installments or a recurring expense appears in every month in
  which it has a charge.

### Main flow

1. The user opens the expenses list from the expenses menu.
2. The system applies the supplied page number and filters, falling back to
   page 0 and no filters for any that are absent.
3. The system validates the parameters.
   *(EF-01 if any parameter is invalid.)*
4. The system retrieves the matching expenses, ordered per FR-03 and windowed
   to the requested page.
5. The system returns the page together with `total_items` and `total_pages`,
   and the user sees the expenses.
   *(AF-01 if the page is empty.)*
6. The user may move to another page or change the filters, returning the flow
   to step 2.
7. The user may select an expense on the page to see its details, leading to
   UC-18.
8. The user may export the list with its current filters, leading to UC-40.

### Alternate flows

#### AF-01 — Empty result
Triggered at step 5 when `items` is empty.

1. If the user has no expenses at all, the system says so ("No expenses yet")
   and explains that expenses are added from a credit card's details (UC-15,
   UC-16) or a bank account's details (UC-16).
2. If the emptiness is caused by the filters, the system says so ("No
   expenses match your search") and offers to clear them.

### Exception flows

#### EF-01 — Invalid parameters
Triggered at step 3 when `page` is negative or not an integer, when a date
bound is not a valid date, when a value bound is not a valid amount, when
`recurring` is not `true` or `false`, when `installments` is not an integer
of at least 1, when a month bound is not a valid `YYYY-MM`, when
`source_type`, `payment_method` or `status` is not one of its allowed values, when `source_id` is supplied without `source_type`, or when a
range's upper bound is below its lower bound.

1. The system does not return a page.
2. The system rejects the request, naming the offending parameter ("End date
   must not be earlier than start date"), and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no expense data whatsoever and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — A user with 25 expenses across several sources and requesting no page
  receives 10 items, most recent first, `page` 0, `total_items` 25,
  `total_pages` 3.
- AC-02 — Paging from 0 to the last page visits every expense exactly once.
- AC-03 — Page 3 of that set returns an empty `items` array with
  `total_items` 25 — not an error.
- AC-04 — A user with no expenses receives an empty `items` array,
  `total_items` 0, `total_pages` 1.
- AC-05 — Two users each with expenses see only their own, and neither user's
  `total_items` includes the other's.
- AC-06 — `source_type=credit_card&source_id=` one of the user's cards
  returns only that card's expenses, card purchases and Pix alike; set to
  another user's card, a non-existent id, or `abc`, it returns an empty page,
  not an error.
- AC-07 — `from_date=2026-09-01&to_date=2026-09-30` returns expenses dated in
  September 2026 inclusive. An expense dated 10 August in 3 installments is
  not returned, although its second installment falls on 10 September.
- AC-08 — `min_value=100&max_value=500` returns expenses with a total value
  from `100.00` to `500.00` inclusive; a `1200.00` expense in 12
  installments of `100.00` is not returned.
- AC-09 — `recurring=true` returns only recurring expenses; `recurring=false`
  returns only non-recurring ones.
- AC-10 — `installments=12` returns only expenses in 12 installments.
- AC-11 — `recurring=false&min_value=100&from_date=2026-09-01` returns only
  the expenses satisfying all three filters.
- AC-12 — `page=-1`, `installments=0`, `recurring=maybe`, and a `to_date`
  earlier than `from_date` are each rejected with EF-01.
- AC-13 — Opening an expense from page 2 of a filtered list and returning
  lands on page 2 with the same filters.
- AC-14 — `payment_method=pix` returns every Pix, whether paid from a bank
  account or on a credit card.
- AC-15 — `payment_method=pix&source_type=bank_account` returns only the Pix
  paid from bank accounts, none of which relate to a credit card.
- AC-16 — `source_id` without `source_type`, and `payment_method=boleto`, are
  each rejected with EF-01.
- AC-17 — `status=stopped` returns only stopped recurring expenses;
  `status=active` returns every other expense, one-off and in installments
  included.
- AC-18 — A removed expense never appears, even when every filter matches
  it; a stopped one appears marked as stopped when `status` is omitted.
- AC-19 — `from_month=2026-10&to_month=2026-10` returns a purchase dated
  29 September assigned to October, and a purchase of August in 3
  installments whose third charge belongs to October.
- AC-20 — `from_date=2026-09-01&to_date=2026-09-30&from_month=2026-10`
  returns only expenses dated in September whose charges include October.
- AC-21 — `status=scheduled` returns only the Pix scheduled for a future
  date.
- AC-22 — `category_id` set to "Groceries" returns only grocery expenses;
  `description=netf` returns "Netflix".
- AC-23 — `internal_transfer=false` hides the Pix between the user's own
  accounts.


## UC-21 — Remove an expense

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-15, UC-16, UC-18, UC-22, Glossary

### Objective

Allow an authenticated user to remove one of their own expenses, after being
warned that the removal cannot be undone and changes the balance of the
funding source or the closing of past accounting months. For a recurring
expense, the user may instead remove the
charge of a single accounting month — the current one or any other — and
keep the expense in every other month.

### Actors

- **User** (primary) — the owner of the funding source and of the expense.
- **System** (secondary) — warns the user, enforces ownership, logically
  removes the expense or the chosen month, and recalculates the funding
  source's balance.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the expense, or the chosen month of a recurring
  expense, is removed as defined in the Glossary: it no longer appears in any
  of the user's queries and no longer counts towards any balance.
- POST-02 — On failure or cancellation, nothing is removed and no balance
  changes.

### Data

#### Request

| Parameter | Type | Required            | Constraints                                                    |
|-----------|------|---------------------|----------------------------------------------------------------|
| `id`      | text | Yes                 | Any string; identifies the expense (as in UC-18)               |
| `scope`   | text | Recurring only      | `single_month` or `whole_expense`                              |
| `month`   | text | With `single_month` | Accounting month, `YYYY-MM`, in which the expense has a charge |

`scope` is required for a recurring expense and must not be supplied for any
other expense. `month` is required with `single_month` and must not be
supplied otherwise.

### Functional requirements

- FR-01 — The system removes an expense only for the user who owns its
  funding source.
- FR-02 — Removal is logical: the system keeps the record, but a removed
  expense or month never appears in any of the user's queries — details,
  lists, searches — and never counts towards any balance (Glossary). The
  user cannot undo a removal.
- FR-03 — Every removal is preceded by a warning, and nothing is removed
  unless the user confirms it. The warning states that:
  - the action cannot be undone;
  - for charges in open accounting months, it changes the balance of the
    bank account, when the expense is funded by a bank account
    (a Pix paid from its balance), or the bill of the credit card, when it is
    funded by a credit card (a card purchase or a Pix on the card's limit);
  - for charges in consolidated accounting months, it changes the
    transaction history and the closing of those months, naming them, and
    not the current balance.
- FR-04 — A one-off expense or an expense in installments is removed as a
  whole, with every installment.
- FR-05 — For a recurring expense — a recurring card purchase or a recurring
  Pix, from either source — the system asks the user, before the warning,
  whether to remove:
  - **a single month** — the charge of the chosen accounting month is
    removed; the expense remains, with its charges in every other month;
  - **the whole expense** — the expense and every one of its charges are
    removed.
- FR-06 — For a single month, the current accounting month is offered by
  default. The user may choose any other accounting month in which the
  expense has, or will have, a charge:
  - a past month, to take out a charge that should not have counted;
  - a future month, to foresee a charge that should not happen.
  A month before the expense's first charge, a month in which the expense is
  stopped, or a month already removed cannot be chosen.
- FR-07 — Removing the current month's charge, once its date has been
  reached, gives its value back to the funding source's current balance.
  Removing a month that is consolidated corrects its closing and leaves the
  current balance unchanged (Glossary, "Changes to the past"); removing an
  earlier month that is still open changes the current balance. Removing a future
  month prevents that charge: nothing is counted when its date arrives.
- FR-08 — To make a recurring expense stop being charged while keeping it
  visible, the user stops it instead (UC-22). The scope question reminds the
  user of that option.
- FR-09 — After a removal, the removed amounts are taken out of the
  accounting month they belong to: from the funding source's current balance
  when they belong to an open accounting month — a card's per UC-15 FR-06, a
  bank account's per UC-16 FR-04 — or from the closing of a consolidated
  accounting month otherwise (UC-36 FR-06).
- FR-10 — Removing a month that is already removed changes nothing and
  succeeds. Removing an expense that is already removed produces the "not
  found" response, as the expense no longer exists for the user.
- FR-11 — Any id that does not match an expense funded by a source the user
  owns produces the same "not found" response as UC-18 FR-02.
- FR-12 — The removal is started from the expense's details (UC-18).
- FR-13 — Removing a scheduled Pix (UC-17) before its date cancels it:
  nothing has counted, so no balance changes.
- FR-14 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On the expense's details (UC-18), the user chooses to remove the expense.
   *(AF-01 if the expense is recurring.)*
2. The system displays the warning (FR-03), naming the bank account or credit
   card whose balance or bill will change.
3. The user confirms the removal.
   *(AF-02 if the user cancels.)*
4. The system retrieves the expense with that id, restricted to expenses
   funded by sources the user owns and not removed.
   *(EF-01 if no such expense is found.)*
5. The system validates the request.
   *(EF-02 if `scope` or `month` is missing, invalid, not allowed, or
   supplied when it should not be.)*
6. The system logically removes the expense or the chosen month (FR-02,
   FR-04 to FR-07) and recalculates the funding source's balance (FR-09).
7. The system confirms: "Expense removed", or "Expense removed from <month>;
   it continues in the other months".
8. The user returns to where they opened the expense from — the funding
   source's details or the expenses list — or, when only a month was
   removed, to the expense's details.

### Alternate flows

#### AF-01 — Choose what to remove in a recurring expense
Triggered at step 1 when the expense is recurring.

1. The system asks whether to remove a single month or the whole expense,
   with the current accounting month preselected, and reminds the user that
   the expense can be stopped instead (FR-05, FR-06, FR-08).
2. The user chooses the scope and, for a single month, optionally another
   accounting month. The flow continues at step 2, and the warning also
   states what will be removed.
3. If the user chooses to stop the expense instead, the flow continues in
   UC-22.
4. If the user cancels, nothing is removed and the user stays on the
   expense's details.

#### AF-02 — Removal cancelled
Triggered at step 3 when the user does not confirm the warning.

1. Nothing is sent, removed or changed, and the user stays on the expense's
   details.

### Exception flows

#### EF-01 — Expense not found
Triggered at step 4 when no expense funded by a source the user owns has that
id — because no expense has it, it has already been removed, or it belongs
to another user.

1. The system removes nothing.
2. The system displays "Expense not found" and offers a way back to the
   expenses list, and the flow ends.

#### EF-02 — Invalid scope or month
Triggered at step 5 when `scope` is missing or not an allowed value for a
recurring expense, when `month` is missing or malformed with
`single_month`, when `month` cannot be chosen (FR-06), or when `scope` or
`month` is supplied when it should not be.

1. The system removes nothing.
2. The system rejects the request, naming the offending parameter, and the
   flow returns to step 1.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system removes nothing and directs the user to authenticate.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-14).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Choosing to remove any expense shows the warning before anything is
  removed; cancelling it leaves the expense and every balance unchanged.
- AC-02 — The warning for a Pix paid from a bank account says the action
  cannot be undone and that the account's balance will change; the warning
  for a card purchase or a Pix on a card says the action cannot be undone and
  that the card's bill will change.
- AC-03 — Removing a one-off Pix of `200.00` made this month, from an
  account with balance `800.00`, makes the balance `1000.00`.
- AC-04 — Removing a card purchase of `1200.00` in 12 installments made this
  month removes every installment and raises the card's balance by
  `1200.00`.
- AC-05 — A removed expense no longer appears in its details (EF-01 in
  UC-18), in its source's details, or in the expenses list with any filters.
- AC-06 — Removing a non-recurring expense never asks for a scope or month.
- AC-07 — A recurring Pix of `50.00` on the 15th, from January, viewed on
  20 September: removing only September gives `50.00` back to the balance,
  keeps January to August, and charges again on 15 October.
- AC-08 — The same expense, with January to August consolidated: removing
  only May corrects the May closing, leaves the current balance unchanged, and May no longer appears in its schedule;
  removing only December means nothing is counted on 15 December, and
  January is charged normally.
- AC-09 — The same expense, with January to August consolidated: removing
  the whole expense gives `50.00` back to the current balance for September,
  corrects the closings of January to August, and the expense disappears from every query.
- AC-10 — A month before the first charge, a month in which the expense was
  stopped, and a malformed month such as `2026-13` are each rejected with
  EF-02.
- AC-11 — Removing an already removed month succeeds and changes nothing;
  removing an already removed expense produces EF-01.
- AC-12 — Removing another user's expense, a non-existent id, and an
  arbitrary string such as `abc` all produce the identical EF-01 response,
  and nothing is removed.
- AC-13 — An unauthenticated request removes nothing.
- AC-14 — With August consolidated, removing a one-off Pix of `200.00` of
  August corrects the August closing, and the account's current balance
  stays the same; the warning names August. With August still open, the same
  removal gives `200.00` back to the current balance.
- AC-15 — Removing a Pix scheduled for next week cancels it, and no balance
  changes.


## UC-22 — Stop a recurring expense or income

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-18, UC-21, UC-23, UC-25, UC-28, Glossary

### Objective

Allow an authenticated user to stop one of their own recurring expenses —
a recurring card purchase or a recurring Pix, from either source — so that it
no longer generates charges, while it stays visible and can be resumed
later.

### Actors

- **User** (primary) — the owner of the funding source and of the expense.
- **System** (secondary) — enforces ownership and stops the expense.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the expense is stopped as defined in the Glossary: it
  remains visible, marked as stopped, generates no charges while stopped,
  and every earlier charge remains and keeps counting.
- POST-02 — On failure or cancellation, nothing changes.

### Data

#### Request

| Parameter | Type | Required | Constraints                                      |
|-----------|------|----------|--------------------------------------------------|
| `id`      | text | Yes      | Any string; identifies the expense (as in UC-18) |

#### Response

The stopped expense, with the same fields as UC-18: `status` is `stopped`, a
new stop period starts on the stop date, and `next_charge_date` is empty.

### Functional requirements

- FR-01 — The system stops an expense only for the user who owns its funding
  source.
- FR-02 — Only an active recurring expense can be stopped.
- FR-03 — Stopping takes effect on the day it is done: no charge dated after
  that day is made while the expense is stopped. Charges dated on or before
  it — including the current month's, if its date has been reached — remain
  and keep counting towards the funding source's balance.
- FR-04 — Stopping removes nothing. The expense remains visible in its
  details, in its source's details and in every list and search, marked as
  stopped (Glossary).
- FR-05 — A stopped expense can be resumed (UC-23). Until then, nothing new
  counts towards the balance.
- FR-06 — Before stopping, the system asks the user to confirm, stating that
  the expense will not be charged while stopped and can be resumed later.
- FR-07 — Stopping an expense that is already stopped changes nothing and
  succeeds, keeping the original stop date.
- FR-08 — Any id that does not match an expense funded by a source the user
  owns produces the same "not found" response as UC-18 FR-02, including a
  removed expense.
- FR-09 — Stopping is started from the expense's details (UC-18), or from the
  removal of a recurring expense (UC-21 AF-01).
- FR-10 — A recurring income (UC-24) is stopped in the same way: every rule
  of this use case applies, reading "income" for "expense", "receipt" for
  "charge", and the income's bank account for the funding source. It is
  started from the income's details (UC-25) or from its removal (UC-28).
- FR-11 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On an active recurring expense's details (UC-18), the user chooses to stop
   it.
2. The system asks for confirmation (FR-06).
3. The user confirms.
   *(If the user cancels, nothing changes and the flow ends.)*
4. The system retrieves the expense with that id, restricted to expenses
   funded by sources the user owns and not removed.
   *(EF-01 if no such expense is found.)*
5. The system checks that the expense is recurring.
   *(EF-02 if it is not.)*
6. The system marks the expense as stopped on the current day (FR-03) and
   confirms: "Expense stopped; it will not be charged until resumed".
7. The user returns to the expense's details (UC-18), which show it as
   stopped, with no next charge.

### Exception flows

#### EF-01 — Expense not found
Triggered at step 4 when no expense funded by a source the user owns has that
id, or it has been removed.

1. The system changes nothing.
2. The system displays "Expense not found" and offers a way back to the
   expenses list, and the flow ends.

#### EF-02 — Expense is not recurring
Triggered at step 5 when the expense is one-off or in installments.

1. The system changes nothing.
2. The system rejects the request with "Only recurring expenses can be
   stopped", and the flow ends.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-11).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — A recurring Pix of `50.00` charged on the 15th, from January,
  stopped on 20 September: the charges of January to September remain and
  keep counting, and no charge is made on 15 October or after while it stays
  stopped.
- AC-02 — The same expense stopped on 10 September: the charges of January to
  August remain and no charge is made on 15 September.
- AC-03 — After stopping, the expense's details show `status` `stopped` and
  no next charge date, and the expense still appears in its source's details
  and in the expenses list, marked as stopped.
- AC-04 — Stopping asks for confirmation; cancelling leaves the expense
  active.
- AC-05 — Stopping an already stopped expense succeeds and keeps the
  original stop date.
- AC-06 — Stopping a one-off expense or an expense in installments fails with
  EF-02.
- AC-07 — Stopping a removed expense, another user's expense, a non-existent
  id, and an arbitrary string such as `abc` all produce the identical EF-01
  response, and nothing changes.
- AC-08 — An unauthenticated request changes nothing.
- AC-09 — A recurring "Salary" of `5000.00` on the 5th, stopped on
  20 September, keeps its receipts up to September and receives nothing on
  5 October; it still appears in the incomes list, marked as stopped.


## UC-23 — Resume a recurring expense or income

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-18, UC-22, UC-25, Glossary

### Objective

Allow an authenticated user to resume one of their own stopped recurring
expenses, so that it is charged and counted again from its next charge date
on.

### Actors

- **User** (primary) — the owner of the funding source and of the expense.
- **System** (secondary) — enforces ownership and makes the expense active
  again.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the expense is active again, its current stop period
  ends on the resumption date, and it is charged again from its first charge
  date after the resumption.
- POST-02 — On failure or cancellation, nothing changes.

### Data

#### Request

| Parameter | Type | Required | Constraints                                      |
|-----------|------|----------|--------------------------------------------------|
| `id`      | text | Yes      | Any string; identifies the expense (as in UC-18) |

#### Response

The resumed expense, with the same fields as UC-18: `status` is `active`, the
latest stop period has its `resumed_on` set, and `next_charge_date` shows the
first charge after the resumption.

### Functional requirements

- FR-01 — The system resumes an expense only for the user who owns its
  funding source.
- FR-02 — Only a stopped recurring expense can be resumed.
- FR-03 — Resuming takes effect on the day it is done. The expense is charged
  again from its first charge date on or after that day, on its usual day of
  the month (UC-15 FR-05).
- FR-04 — Months in which the expense was stopped remain without charges.
  Resuming never creates charges for the stopped period.
- FR-05 — The value, funding source and other data of the expense are
  unchanged by stopping and resuming.
- FR-06 — Before resuming, the system asks the user to confirm, stating the
  date of the next charge.
- FR-07 — Resuming an expense that is already active changes nothing and
  succeeds.
- FR-08 — Any id that does not match an expense funded by a source the user
  owns produces the same "not found" response as UC-18 FR-02, including a
  removed expense.
- FR-09 — An expense may be stopped and resumed any number of times. Each
  stop is kept as a stop period shown in the expense's details (UC-18).
- FR-10 — Resuming is started from the details of a stopped expense (UC-18).
- FR-11 — A stopped recurring income (UC-24) is resumed in the same way,
  reading "income" for "expense" and "receipt" for "charge", from the
  income's details (UC-25).
- FR-12 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a stopped recurring expense's details (UC-18), the user chooses to
   resume it.
2. The system asks for confirmation, showing the next charge date (FR-06).
3. The user confirms.
   *(If the user cancels, nothing changes and the flow ends.)*
4. The system retrieves the expense with that id, restricted to expenses
   funded by sources the user owns and not removed.
   *(EF-01 if no such expense is found.)*
5. The system checks that the expense is recurring.
   *(EF-02 if it is not.)*
6. The system makes the expense active again, ends the current stop period on
   the current day, and confirms: "Expense resumed; next charge on <date>".
7. The user returns to the expense's details (UC-18), which show it as active
   with its next charge date.

### Exception flows

#### EF-01 — Expense not found
Triggered at step 4 when no expense funded by a source the user owns has that
id, or it has been removed.

1. The system changes nothing.
2. The system displays "Expense not found" and offers a way back to the
   expenses list, and the flow ends.

#### EF-02 — Expense is not recurring
Triggered at step 5 when the expense is one-off or in installments.

1. The system changes nothing.
2. The system rejects the request with "Only recurring expenses can be
   resumed", and the flow ends.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

#### EF-04 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-12).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — A recurring Pix of `50.00` on the 15th, stopped on 20 June and
  resumed on 10 September: no charges in July and August, and it is charged
  again on 15 September.
- AC-02 — The same expense resumed on 20 September instead: the next charge
  is 15 October, and September has no charge.
- AC-03 — Resuming never adds charges for the months in which the expense was
  stopped, and the balance counts only charges made while active.
- AC-04 — After resuming, the details show `status` `active` and a stop
  period from 20 June to the resumption date.
- AC-05 — Stopping and resuming twice leaves two stop periods, each without
  charges.
- AC-06 — Resuming asks for confirmation showing the next charge date;
  cancelling leaves the expense stopped.
- AC-07 — Resuming an already active expense succeeds and changes nothing.
- AC-08 — Resuming a one-off expense or an expense in installments fails with
  EF-02.
- AC-09 — Resuming a removed expense, another user's expense, a non-existent
  id, and an arbitrary string such as `abc` all produce the identical EF-01
  response, and nothing changes.
- AC-10 — An unauthenticated request changes nothing.
- AC-11 — The "Salary" stopped on 20 September and resumed on 1 November
  receives nothing in October and receives again on 5 November.

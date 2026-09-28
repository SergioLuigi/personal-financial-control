# Credit card bills — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-29 to UC-32 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-29 — View a credit card bill](#uc-29--view-a-credit-card-bill)
- [UC-30 — Pay a credit card bill](#uc-30--pay-a-credit-card-bill)
- [UC-31 — Remove a bill payment](#uc-31--remove-a-bill-payment)
- [UC-32 — Register a credit card refund](#uc-32--register-a-credit-card-refund)


## UC-29 — View a credit card bill

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-07, UC-30, UC-31, UC-32, UC-37, Glossary

### Objective

Allow an authenticated user to see a credit card's bill for an accounting
month: what makes it up, when it is due, what has been paid and what is still
owed.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — gathers the bill's lines and works out its totals
  and status.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter    | Type    | Required | Constraints                                                 |
|--------------|---------|----------|-------------------------------------------------------------|
| `card_id`    | text    | Yes      | One of the user's credit cards                              |
| `month`      | text    | No       | `YYYY-MM`; defaults to the current accounting month         |
| `lines_page` | integer | No       | ≥ 0, defaults to `0`                                        |

#### Response

| Field           | Type          | Notes                                                                        |
|-----------------|---------------|------------------------------------------------------------------------------|
| `card`          | object        | `id`, `name`                                                                 |
| `month`         | text          | `YYYY-MM`                                                                    |
| `due_date`      | date          | Glossary, "Due date"                                                         |
| `status`        | text          | `open`, `closed`, `partially_paid`, `paid` or `overdue`                      |
| `lines`         | page          | Charges and refunds of the month: `date`, `description`, `category`, `amount` (negative for refunds), `installment`; 10 per page |
| `carried`       | decimal(13,2) | Amount carried from the previous bill                                        |
| `total`         | decimal(13,2) | Sum of the lines and the carried amount                                      |
| `payments`      | array         | `date`, `amount`, `bank_account`                                             |
| `paid`          | decimal(13,2) | Sum of the payments                                                          |
| `outstanding`   | decimal(13,2) | `total` − `paid`                                                             |

### Functional requirements

- FR-01 — The system shows a bill only for a card the user owns; any other
  `card_id` produces the "not found" response of UC-07 FR-02.
- FR-02 — The bill is made up as in the Glossary ("Bill"): the card's charges
  of the month — including installments and recurring charges of earlier
  purchases — its refunds, and the amount carried from the previous bill. Removed entries and removed months never appear.
- FR-03 — A bill of a future month is a forecast, built as in UC-37 FR-04,
  and cannot be paid yet unless the user chooses to pay it in advance
  (UC-30).
- FR-04 — The user can move to the previous or the next bill of the same
  card.
- FR-05 — The bill is reached from the card's details (UC-07), from an
  accounting month's view (UC-37), or from a bill due reminder (UC-38).

### Main flow

1. The user opens a card's bill.
2. The system validates the request.
   *(EF-01 if the card is not found; EF-02 if a parameter is invalid.)*
3. The system gathers the bill (FR-02) and shows it, with its due date,
   status, total, payments and outstanding amount.
4. The user may move to another bill or page, returning to step 2; open a
   line's expense (UC-18); pay the bill (UC-30); or remove a payment
   (UC-31).

### Exception flows

#### EF-01 — Card not found
Triggered at step 2 when no card of the user has that id.

1. The system shows no bill and displays "Credit card not found".

#### EF-02 — Invalid parameters
Triggered at step 2 when `month` or `lines_page` is invalid.

1. The system shows no bill and names the offending parameter.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system shows no data and directs the user to authenticate.

### Acceptance criteria

- AC-01 — A card with a `300.00` purchase, the 2nd `100.00` installment of an
  earlier purchase and a `50.00` refund in September shows a September bill
  of `350.00`.
- AC-02 — With closing day 30 and due day 10, the September bill is due on
  10 October.
- AC-03 — After a `200.00` payment, the bill shows `paid` `200.00`,
  `outstanding` `150.00` and status `partially_paid`.
- AC-04 — On 11 October, with `150.00` still unpaid, the status is `overdue`.
- AC-05 — The November bill shows the forecast installments and recurring
  charges that will fall in it.


## UC-30 — Pay a credit card bill

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-06, UC-29, UC-31, Glossary

### Objective

Allow an authenticated user to register the payment of a credit card bill,
in full or in part, from the bank account the card is associated with, so that the account's
balance and the card's available limit reflect it.

### Actors

- **User** (primary) — the owner of the card and of the bank account.
- **System** (secondary) — validates the payment, updates both balances and
  the bill's status.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the payment is recorded: the bank account's balance
  falls, the card's available limit rises by the amount paid, and the bill's
  status reflects it.
- POST-02 — On failure or cancellation, nothing changes.

### Data

| Field              | Type          | Required | Constraints                                                     |
|--------------------|---------------|----------|-----------------------------------------------------------------|
| `card_id`          | text          | Yes      | One of the user's credit cards                                  |
| `month`            | text          | Yes      | `YYYY-MM`; the bill being paid                                  |
| `amount`           | decimal(13,2) | Yes      | Greater than `0.00`, at most the outstanding amount; defaults to it |
| `date`             | date          | Yes      | Not in the future; defaults to today                            |
| `accounting_month` | text          | No       | `YYYY-MM`; defaults to the current one; must exist              |

### Functional requirements

- FR-01 — A payment always comes from the card's own bank account (UC-06
  FR-03); the user does not choose it. It takes `amount` from that account
  and releases the same amount of the card's limit (UC-06 FR-09).
- FR-02 — A payment may be partial. Whatever is still unpaid on the due date
  is carried to the next bill as it is, with no fee or interest
  (UC-43).
- FR-03 — A bill may be paid at any time: before its due date, before its
  accounting month is consolidated, and in several payments.
- FR-04 — A payment is not an expense. It is counted in no total spent or
  received, is listed in the bank account's details marked as a bill payment
  (UC-03), and appears in the month's view and closing (UC-36, UC-37).
- FR-05 — The bank account's balance may become negative.
- FR-06 — The payment belongs to an accounting month, by default the current
  one; it must exist, or the user is required to create it first (UC-33).
  "Changes to the past" and the pending month lock apply (Glossary).
- FR-07 — Paying is started from the bill (UC-29), the card's details
  (UC-07), a bill due reminder (UC-38) or a payment line of an imported
  statement (UC-39).
- FR-08 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. The user chooses to pay a bill.
2. The system shows the bill's outstanding amount and due date, the card's
   bank account it will be paid from, and the full amount preselected.
3. The user confirms or changes the amount, the date and
   the accounting month.
   *(AF-01 if the chosen month does not exist.)*
4. The user confirms.
   *(AF-02 if the chosen accounting month is consolidated.)*
5. The system validates the payment.
   *(EF-01 if the card or bill is not found; EF-02 if the amount is invalid;
   EF-03 if the date is invalid.)*
6. The system records the payment, lowers the account's balance, releases
   the card's limit, updates the bill's status, and confirms: "Payment of
   <amount> registered for the <month> bill".
7. The user returns to the bill.

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 3 when the chosen month does not exist.

1. The system requires the user to create it (UC-33) before continuing.

#### AF-02 — Consolidated month
Triggered at step 4 when the chosen accounting month is consolidated.

1. The system shows the past entry warning (UC-19 FR-12), naming the month:
   as a new entry, it changes the current balance and is recorded as a
   correction in that month's closing (Glossary, "Changes to the past").
2. If the user acknowledges, the flow continues; if not, it returns to
   step 3 and nothing is registered.

### Exception flows

#### EF-01 — Not found
Triggered at step 5 when the card or the bill's month is not one of the
user's.

1. The system records nothing and names what was not found.

#### EF-02 — Invalid amount
Triggered at step 5 when the amount is not a valid positive amount, or
exceeds the outstanding amount.

1. The system records nothing and displays "Amount must be between 0.01 and
   <outstanding>"; the flow returns to step 3.

#### EF-03 — Invalid date
Triggered at step 5 when the date is invalid or in the future.

1. The system records nothing; the flow returns to step 3.

#### EF-04 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system records nothing and directs the user to authenticate.

#### EF-05 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-08).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Paying `350.00` of a `350.00` bill from an account with balance
  `1000.00` makes the account `650.00`, raises the card's available limit by
  `350.00`, and marks the bill `paid`.
- AC-02 — Paying `200.00` marks it `partially_paid`; on the due date the
  `150.00` left is carried to the next bill, with nothing added.
- AC-03 — A payment before the due date and before consolidation succeeds.
- AC-04 — Paying more than the outstanding amount fails with EF-02.
- AC-05 — The payment appears in the account's details as a bill payment and
  in no total spent or received.
- AC-06 — A payment always comes from the card's bank account; sending
  another `bank_account_id` fails.


## UC-31 — Remove a bill payment

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-29, UC-30, Glossary

### Objective

Allow an authenticated user to remove a bill payment registered by mistake,
after being warned of its effect on the bank account and the card.

### Actors

- **User** (primary) — the owner of the card and of the bank account.
- **System** (secondary) — warns the user and removes the payment.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the payment is removed (Glossary, "Removed"): the
  bank account's balance rises, the card's available limit falls by the same
  amount, and the bill's status is recalculated.
- POST-02 — On failure or cancellation, nothing changes.

### Data

| Parameter    | Type | Required | Constraints                  |
|--------------|------|----------|------------------------------|
| `payment_id` | text | Yes      | A payment of one of the user's bills |

### Functional requirements

- FR-01 — Removal is logical and cannot be undone, as for expenses (UC-21
  FR-02).
- FR-02 — Before removing, the system warns that the action cannot be undone,
  that the bank account's balance will rise and the card's available limit
  fall by the amount, and that the bill may become partially paid or overdue.
- FR-03 — "Changes to the past" applies to the payment's accounting month.
- FR-04 — Removing is started from the bill (UC-29).
- FR-05 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a bill, the user chooses to remove one of its payments.
2. The system shows the warning (FR-02).
3. The user confirms.
   *(If the user cancels, nothing changes.)*
4. The system removes the payment, recalculates both balances and the bill's
   status, and confirms: "Payment removed".

### Exception flows

#### EF-01 — Payment not found
Triggered at step 4 when no payment of the user's bills has that id.

1. The system changes nothing and displays "Payment not found".

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

#### EF-03 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-05).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — Removing a `350.00` payment raises the account's balance by
  `350.00`, lowers the card's available limit by `350.00`, and the bill
  becomes `closed`, `open` or `overdue` again.
- AC-02 — Removing asks for confirmation; cancelling changes nothing.


## UC-32 — Register a credit card refund

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-07, UC-18, UC-29, Glossary

### Objective

Allow an authenticated user to register a credit on one of their cards, such
as a reversed purchase, so that the bill and the available limit reflect it.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — validates and records the refund.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the refund lowers the bill of its accounting month,
  releases the card's limit, and lowers the total spent in its category.
- POST-02 — On failure, nothing is persisted.

### Data

| Field              | Type          | Required | Constraints                                                     |
|--------------------|---------------|----------|-----------------------------------------------------------------|
| `card_id`          | text          | Yes      | One of the user's credit cards                                  |
| `description`      | text          | Yes      | 1–100 chars, trimmed                                            |
| `value`            | decimal(13,2) | Yes      | Greater than `0.00`                                             |
| `date`             | date          | Yes      | Not in the future                                               |
| `expense_id`       | text          | No       | The refunded expense, on the same card                          |
| `category_id`      | text          | Yes      | An expense category; defaults to the refunded expense's         |
| `accounting_month` | text          | No       | `YYYY-MM`; defaults to the current one; must exist              |

### Functional requirements

- FR-01 — A refund lowers the bill of its accounting month by its value and
  releases the same amount of the card's limit (UC-06 FR-09).
- FR-02 — A refund lowers the month's total spent and its category's total;
  it is not an income.
- FR-03 — A refund may be linked to the expense it reverses; the link is
  informative and does not change that expense.
- FR-04 — A refund is viewed, changed and removed like an expense (UC-18,
  UC-19, UC-21), and is listed in the expenses list (UC-20) marked as a
  refund, with a negative value.
- FR-05 — "Changes to the past" and the pending month lock apply
  (Glossary).
- FR-06 — Registering is started from the card's details (UC-07) or a credit
  line of an imported statement (UC-39).
- FR-07 — The pending month lock applies (Glossary): while an earlier accounting
  month is pending, the operation is refused in any later month until the
  pending month is consolidated (UC-36). The user is told so as soon as they
  start the operation, not only when they confirm it.

### Main flow

1. On a card's details, the user chooses to register a refund.
2. The user supplies the description, value, date and category, optionally
   the refunded expense and another accounting month, and confirms.
   *(AF-01 if the chosen month does not exist; AF-02 if it is
   consolidated.)*
3. The system validates them.
   *(EF-01 if invalid.)*
4. The system records the refund, updates the bill and the available limit,
   and confirms: "Refund registered".

### Alternate flows

#### AF-01 — Accounting month not created
Triggered at step 2 when the chosen accounting month does not exist.

1. The system requires the user to create it (UC-33) before continuing; if
   the user does not, nothing is registered.

#### AF-02 — Consolidated month
Triggered at step 2 when the chosen accounting month is consolidated.

1. The system shows the past entry warning (UC-19 FR-12), naming the month:
   as a new entry, it changes the current balance and is recorded as a
   correction in that month's closing (Glossary, "Changes to the past").
2. If the user acknowledges, the flow continues; if not, it returns to
   step 2 and nothing is registered.

### Exception flows

#### EF-01 — Invalid data
Triggered at step 3 when a field breaks its constraints, or the refunded
expense is not on that card.

1. The system records nothing and names the field; the flow returns to
   step 2.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system records nothing and directs the user to authenticate.

#### EF-03 — Earlier month pending
Triggered as soon as the user starts the operation, and again when they
confirm it, when an accounting month earlier than the one the operation is
made in is pending (FR-07).

1. The system changes nothing and tells the user: "<pending month> is still
   pending. Consolidate it before making changes in later months."
2. The system offers a way to consolidate it (UC-36), and the flow ends.

### Acceptance criteria

- AC-01 — A `50.00` refund in September lowers the September bill by `50.00`
  and raises the available limit by `50.00`.
- AC-02 — Linked to a "Clothing" purchase, it defaults to "Clothing" and
  lowers that category's September total by `50.00`.
- AC-03 — The refund appears in the expenses list as a refund of `-50.00`.

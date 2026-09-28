# Bank accounts — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-01 to UC-05 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-01 — Create a bank account](#uc-01--create-a-bank-account)
- [UC-02 — List bank accounts](#uc-02--list-bank-accounts)
- [UC-03 — View bank account details](#uc-03--view-bank-account-details)
- [UC-04 — Update a bank account](#uc-04--update-a-bank-account)
- [UC-05 — Delete a bank account](#uc-05--delete-a-bank-account)


## UC-01 — Create a bank account

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-02

### Objective

Allow an authenticated user to register a new bank account so that
transactions can later be assigned to it.

### Actors

- **User** (primary) — the account owner.
- **System** (secondary) — validates and persists the account.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, a bank account is persisted and owned by the
  requesting user.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field             | Type          | Required | Constraints                                  |
|-------------------|---------------|----------|----------------------------------------------|
| `name`            | text          | Yes      | 1–60 chars, trimmed, unique per user          |
| `description`     | text          | No       | 0–255 chars                                   |
| `initial_balance` | decimal(13,2) | No       | Defaults to `0.00`; may be negative           |

### Functional requirements

- FR-01 — The system persists a new bank account owned by the requesting user.
- FR-02 — A bank account has a name, an optional description and a balance.
- FR-03 — Account names are unique per user, compared case-insensitively and
  after trimming surrounding whitespace. Two different users may each have an
  account named "Savings".
- FR-04 — When no initial balance is supplied, the balance is `0.00`.
- FR-05 — The balance may be positive, zero or negative.
- FR-06 — Creating a bank account is started from the bank accounts list
  (UC-02), or from the first steps shown to a new user on the home summary
  (UC-38 AF-01).
- FR-07 — The initial balance is stored directly as the account's balance.
  It belongs to no accounting month and appears in no month total or closing
  (Glossary, "Balance adjustment"). The balance can be adjusted again at any
  time (UC-04).

### Main flow

1. On the bank accounts list (UC-02), the user chooses to create a new bank
   account.
2. The system presents an empty bank-account form.
3. The user supplies a name, and optionally a description and an initial
   balance.
4. The user confirms the creation.
5. The system validates the submitted data.
   *(EF-01 if the name is already taken; EF-02 if the name is missing or too
   long; EF-03 if the balance is not a valid amount; EF-04 if the description
   is too long.)*
6. The system persists the account and confirms:
   "Bank account <name> created".

### Exception flows

#### EF-01 — Name already taken
Triggered at step 5 when the user already owns an account with that name.

1. The system does not persist the account.
2. The system displays "Bank account name already exists" next to the name
   field, and the flow returns to step 3.

#### EF-02 — Invalid name
Triggered at step 5 when the name is empty after trimming, or longer than
60 characters.

1. The system does not persist the account.
2. The system displays the corresponding message next to the name field
   ("Name is required" / "Name must be at most 60 characters"), and the flow
   returns to step 3.

#### EF-03 — Invalid balance
Triggered at step 5 when the balance is not a valid amount with at most two
decimal places.

1. The system does not persist the account.
2. The system displays "Enter a valid amount" next to the balance field, and
   the flow returns to step 3.

#### EF-04 — Invalid description
Triggered at step 5 when the description is longer than 255 characters.

1. The system does not persist the account.
2. The system displays "Description must be at most 255 characters" next to
   the description field, and the flow returns to step 3.

### Acceptance criteria

- AC-01 — Creating an account with only a name succeeds, and its balance is
  `0.00`.
- AC-02 — Creating a second account with the same name for the same user
  fails with the EF-01 message, and the account count is unchanged.
- AC-03 — "savings" and " Savings " collide with an existing "Savings" for the
  same user.
- AC-04 — Two different users can each own an account named "Savings".
- AC-05 — An account created with `-150.00` stores `-150.00`.
- AC-06 — An unauthenticated request is rejected and persists nothing.
- AC-07 — A description of 256 characters fails with EF-04.
- AC-08 — Creating an account with an initial balance of `1000.00` makes its
  balance `1000.00`; no accounting month is involved and none needs to
  exist.


## UC-02 — List bank accounts

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-01, UC-03

### Objective

Allow an authenticated user to browse their own bank accounts, ten at a time,
so they can find one to inspect or edit.

### Actors

- **User** (primary) — the account owner.
- **System** (secondary) — filters, orders and paginates the accounts.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter     | Type          | Required | Constraints                                    |
|---------------|---------------|----------|------------------------------------------------|
| `page`        | integer       | No       | ≥ 0, defaults to `0` (the first page)          |
| `name`        | text          | No       | 0–60 chars, trimmed                            |
| `description` | text          | No       | 0–255 chars, trimmed                           |
| `min_balance` | decimal(13,2) | No       | Inclusive lower bound                          |
| `max_balance` | decimal(13,2) | No       | Inclusive upper bound; must be ≥ `min_balance` |

#### Response

| Field         | Type             | Notes                                           |
|---------------|------------------|-------------------------------------------------|
| `items`       | array of account | `id`, `name`, `description`, `balance`; 0–10    |
| `page`        | integer          | Echoes the page returned                         |
| `total_items` | integer          | Accounts matching the filters, across all pages  |
| `total_pages` | integer          | `ceil(total_items / 10)`, minimum `1`           |

### Functional requirements

- FR-01 — The page contains only accounts owned by the requesting user.
- FR-02 — A page contains at most 10 accounts.
- FR-03 — Accounts are ordered by name ascending, with `id` as the final
  tie-breaker, so that ordering is stable: no account appears on two pages or
  is skipped between them.
- FR-04 — Pages are numbered from 0. The system reports `total_pages` so the
  user can move backwards, move forwards, or jump straight to a page number.
- FR-05 — The user may filter by name, by description and by a balance range.
  Name and description match case-insensitively as substrings, after trimming;
  the balance range is inclusive at both ends.
- FR-06 — Filters combine with AND: an account is returned only when it
  satisfies every filter supplied.
- FR-07 — `total_items` and `total_pages` reflect the active filters, not the
  user's whole collection.
- FR-08 — A page beyond the last one returns an empty `items` array with
  correct metadata. It is a valid result, not an error.
- FR-09 — A user with no accounts receives an empty page. This is a valid
  result, not an error.
- FR-10 — The bank accounts list is the only entry point for creating a bank
  account (UC-01). It always offers that option, whether the page has
  accounts, is empty, or is filtered.

### Main flow

1. The user opens the bank accounts list.
2. The system applies the supplied page number and filters, falling back to
   page 0 and no filters for any that are absent.
3. The system validates the parameters.
   *(EF-01 if any parameter is invalid.)*
4. The system retrieves the user's matching accounts, ordered per FR-03 and
   windowed to the requested page.
5. The system returns the page together with `total_items` and `total_pages`,
   and the user sees the accounts.
   *(AF-01 if the page is empty.)*
6. The user may move to another page or change the filters, returning the flow
   to step 2.
7. While on the bank accounts list, the user may choose to create a new bank
   account, leading to UC-01 (FR-10).
8. The user may select an account on the page to see its details, leading to
   UC-03.

### Alternate flows

#### AF-01 — Empty result
Triggered at step 5 when `items` is empty.

1. If the user owns no accounts at all, the system says so ("No bank accounts
   yet"). The create option from FR-10 remains available.
2. If the emptiness is caused by the filters, the system says so ("No accounts
   match your search") and offers to clear them.

### Exception flows

#### EF-01 — Invalid parameters
Triggered at step 3 when `page` is negative or not an integer, when a balance
bound is not a valid amount, or when `max_balance` is below `min_balance`.

1. The system does not return a page.
2. The system rejects the request, naming the offending parameter ("Maximum
   balance must not be lower than minimum balance"), and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no account data whatsoever and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — A user owning 25 accounts and requesting no page receives 10 items,
  `page` 0, `total_items` 25, `total_pages` 3.
- AC-02 — Page 2 of that set returns the remaining 5 items, and no id appears
  on more than one page.
- AC-03 — Page 3 of that set returns an empty `items` array with `total_items`
  25 — not an error.
- AC-04 — A user owning no accounts receives an empty `items` array,
  `total_items` 0, `total_pages` 1.
- AC-05 — Two users each owning accounts see only their own, and neither
  user's `total_items` includes the other's.
- AC-06 — `name=sav` returns "Savings" and "SAVINGS POT" but not "Checking",
  and `total_items` counts only the matches.
- AC-07 — `min_balance=0&max_balance=0` returns every account whose balance is
  exactly `0.00`.
- AC-08 — `name=sav&min_balance=100` returns only the accounts satisfying both
  filters.
- AC-09 — `page=-1`, and a `max_balance` below `min_balance`, are each
  rejected with EF-01.
- AC-10 — Paging from 0 to the last page visits every account exactly once.
- AC-11 — The option to create a bank account is available with accounts
  listed, with no accounts, and with filters that match nothing.


## UC-03 — View bank account details

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-01, UC-02, UC-04, UC-05, UC-06, UC-07, UC-16, UC-17, UC-18, UC-24, UC-25

### Objective

Allow an authenticated user to see the full details of one of their own bank
accounts, including the credit cards associated with it, the Pix paid from
its balance and the incomes received into it.

### Actors

- **User** (primary) — the account owner.
- **System** (secondary) — retrieves the account and enforces ownership.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter       | Type    | Required | Constraints                           |
|-----------------|---------|----------|---------------------------------------|
| `id`            | text    | Yes      | Any string; no format is enforced     |
| `expenses_page` | integer | No       | ≥ 0, defaults to `0` (the first page) |
| `incomes_page`  | integer | No       | ≥ 0, defaults to `0` (the first page) |

#### Response

| Field          | Type          | Notes                                                                 |
|----------------|---------------|-----------------------------------------------------------------------|
| `id`           | text          | The account's id                                                      |
| `name`         | text          | As stored                                                             |
| `description`  | text          | Empty when none was supplied                                          |
| `balance`      | decimal(13,2) | Current balance (Glossary, "Balance"); may be negative               |
| `projected_balance` | decimal(13,2) | Balance minus what is still to be paid on its cards' bills (Glossary) |
| `credit_cards` | array of card | `id`, `name`, `balance` of each card on the account; may be empty     |
| `expenses`     | page          | `items` (`id`, `date`, `value`, `recurring`, `payment_method`, `recipient_name`, `status`; 0–10), `page`, `total_items`, `total_pages` |
| `incomes`      | page          | `items` (`id`, `description`, `date`, `value`, `accounting_month`, `recurring`, `status`; 0–10), `page`, `total_items`, `total_pages` |

### Functional requirements

- FR-01 — The system returns an account only to the user who owns it.
- FR-02 — Any id that does not match an account the user owns produces the
  same "not found" response, whether no account has that id or another user
  owns it. The user cannot learn whether another user's account exists.
- FR-03 — The details show the account's current data, including any changes
  made since the list was loaded.
- FR-04 — The details are reached from the bank accounts list (UC-02), among
  other places such as the home summary (UC-38) or a card's details (UC-07).
- FR-05 — From the details, the user can return to the bank accounts list on the
  same page and with the same filters they left.
- FR-06 — The details list every credit card associated with the account
  (UC-06), ordered by name ascending.
- FR-07 — The details list the expenses funded by the account's balance —
  today, the Pix paid from it (UC-16) — at most 10 per page, ordered by date
  descending with `id` as the final tie-breaker. Paging follows the same rules
  as UC-02 FR-04 and FR-08; an account with no such expenses shows an empty
  list, not an error. Removed expenses never appear; stopped ones appear,
  marked as stopped (Glossary).
- FR-08 — Pix paid on the limit of one of the account's credit cards are not
  listed here: they are funded by the card, not by the account's balance, and
  appear on the card's details (UC-07).
- FR-09 — The details list the incomes received into the account
  (UC-24), with the same paging and ordering rules as FR-07. Removed
  incomes never appear.
- FR-10 — Internal transfers received from another of the user's accounts
  (UC-16 FR-14) are listed with the incomes, marked as transfers, and are not
  counted as income.
- FR-11 — Bill payments made from the account (UC-30) are listed with
  its expenses, marked as bill payments, and are not counted as expenses.

### Main flow

1. On the bank accounts list (UC-02), the user selects an account.
2. The system retrieves the account with that id, restricted to accounts the
   user owns.
   *(EF-01 if no such account is found; EF-03 if `expenses_page` or
   `incomes_page` is negative or not an integer.)*
3. The system displays the account's name, description and balance, the
   credit cards associated with it (FR-06), and the requested pages of its
   expenses (FR-07) and incomes (FR-09).
4. The user returns to the bank accounts list, on the page and with the filters
   they left (FR-05).
5. Alternatively, the user may move to another page of expenses, returning
   the flow to step 2; move to another page of incomes, returning the flow to
   step 2; edit the account, leading to UC-04; add a credit card
   to it, leading to UC-06; select one of its credit cards, leading to UC-07;
   register a Pix paid from its balance, leading to UC-16; schedule a Pix,
   leading to UC-17; select one of its expenses, leading to UC-18; register
   an income, leading to UC-24; select one of its incomes, leading to
   UC-25; or delete the account, leading to UC-05.

### Exception flows

#### EF-01 — Account not found
Triggered at step 2 when no account the user owns has that id — because no
account has it, the account has been deleted, or another user owns it.

1. The system returns no account data.
2. The system displays "Bank account not found" and offers a way back to the
   bank accounts list, and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no account data whatsoever and directs the user to
   authenticate.

#### EF-03 — Invalid page
Triggered at step 2 when `expenses_page` or `incomes_page` is negative or not
an integer.

1. The system does not return the details.
2. The system rejects the request with "Page must be a non-negative integer",
   and the flow ends.

### Acceptance criteria

- AC-01 — Opening one of the user's own accounts shows its id, name,
  description and balance exactly as stored.
- AC-02 — An account created with no description shows an empty description,
  and one created with `-150.00` shows `-150.00`.
- AC-03 — Requesting another user's account, a non-existent id, and an
  arbitrary string such as `abc` all produce the identical EF-01 response,
  never a system error.
- AC-04 — Opening an account from page 2 of a filtered list and returning lands
  on page 2 with the same filters.
- AC-05 — An unauthenticated request returns no account data.
- AC-06 — An account with 25 Pix paid from its balance shows 10 on
  `expenses_page` 0, most recent first, with `total_items` 25 and
  `total_pages` 3; paging through visits every Pix exactly once.
- AC-07 — Each listed Pix shows its date, value, recurring flag and
  recipient.
- AC-08 — A Pix paid on one of the account's credit cards does not appear in
  the account's expenses.
- AC-09 — An account with no Pix shows an empty expenses list.
- AC-10 — An account with incomes lists them, most recent first, 10 per
  page; a removed income does not appear.
- AC-11 — A `1000.00` internal transfer from account A appears in account B's
  incomes list marked as a transfer.
- AC-12 — An account with balance `2000.00` whose card has `350.00` still to
  pay shows a projected balance of `1650.00`.


## UC-04 — Update a bank account

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-01, UC-03

### Objective

Allow an authenticated user to change the name, description and balance of
one of their own bank accounts through a partial update that is idempotent:
sending the same request once or many times leaves the account in the same
state.

### Actors

- **User** (primary) — the account owner.
- **System** (secondary) — validates the changes, enforces ownership and
  persists the account.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the supplied fields hold the new values and every
  other field is unchanged.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

#### Request

| Parameter     | Type          | Required | Constraints                                        |
|---------------|---------------|----------|----------------------------------------------------|
| `id`          | text          | Yes      | Any string; identifies the account (as in UC-03)   |
| `name`        | text          | No       | 1–60 chars, trimmed, unique per user               |
| `description` | text          | No       | 0–255 chars; an empty value clears the description |
| `balance`     | decimal(13,2) | No       | Absolute new balance; may be negative              |

Only `name`, `description` and `balance` may be supplied. `id` cannot be
changed through this use case.

#### Response

The updated account, with the same fields as UC-03: `id`, `name`,
`description`, `balance`.

### Functional requirements

- FR-01 — The system updates an account only for the user who owns it.
- FR-02 — The update is partial: a field left out of the request keeps its
  current value.
- FR-03 — Every supplied field carries the absolute new value, never a
  relative change. Repeating the same request therefore produces the same
  account and the same response as sending it once.
- FR-04 — Name uniqueness follows UC-01 FR-03 (case-insensitive, after
  trimming), excluding the account being updated. Keeping the current name,
  or changing only its capitalization, is allowed.
- FR-05 — A request that changes nothing — no fields supplied, or values equal
  to the current ones — succeeds and returns the account unchanged.
- FR-06 — A request containing a field other than `name`, `description` or
  `balance`, such as `id`, is rejected as a whole; nothing is changed.
- FR-07 — Any id that does not match an account the user owns produces the
  same "not found" response as UC-03 FR-02.
- FR-08 — The update is started from the account's details (UC-03).
- FR-09 — The balance is set to the supplied value, never adjusted by it:
  sending `balance=500.00` twice leaves the balance at `500.00`.
- FR-10 — When the user changes the balance, the system displays a warning
  about the consequences before the update is sent, and the update proceeds
  only if the user acknowledges it. The warning is not shown when the balance
  is left unchanged. The warning reads:

  > The balance of [account] will change from [A] to [B], a difference of
  > [X].
  > This is a manual **adjustment**: it is neither an expense nor an income,
  > and it is not counted in any month's totals. If you know where the
  > difference came from, register that expense or income instead.
- FR-11 — A balance change is a balance adjustment (Glossary): the new value
  is applied directly to the stored balance, at any time. It belongs to no
  accounting month, appears in no month total or closing, and is not
  subject to the pending month lock.

### Main flow

1. On the account's details (UC-03), the user chooses to edit the account.
2. The system presents a form pre-filled with the current name, description
   and balance.
3. The user changes any of the name, the description and the balance.
4. The user confirms the update.
   *(AF-01 if the balance was changed.)*
5. The system retrieves the account with that id, restricted to accounts the
   user owns.
   *(EF-01 if no such account is found.)*
6. The system validates the supplied fields.
   *(EF-02 if an unchangeable field is supplied; EF-03 if the name is taken;
   EF-04 if the name is missing or too long; EF-05 if the description is too
   long; EF-07 if the balance is not a valid amount.)*
7. The system persists the supplied fields, leaving the others untouched, and
   confirms: "Bank account <name> updated".
8. The user returns to the account's details (UC-03), which show the new
   values.

### Alternate flows

#### AF-01 — Balance change warning
Triggered at step 4 when the balance differs from the current one.

1. Before sending the update, the system displays a warning explaining the
   consequences of changing the balance (FR-10).
2. If the user acknowledges the warning, the flow continues at step 5.
3. If the user cancels, nothing is sent or changed, and the flow returns to
   step 3 with the entered values kept.

### Exception flows

#### EF-01 — Account not found
Triggered at step 5 when no account the user owns has that id.

1. The system changes nothing and returns no account data.
2. The system displays "Bank account not found" and offers a way back to the
   bank accounts list, and the flow ends.

#### EF-02 — Unchangeable field
Triggered at step 6 when the request contains a field other than `name`,
`description` or `balance`.

1. The system changes nothing.
2. The system rejects the request, naming the field ("id cannot be
   changed"), and the flow ends.

#### EF-03 — Name already taken
Triggered at step 6 when another account of the same user already has that
name.

1. The system changes nothing.
2. The system displays "Bank account name already exists" next to the name
   field, and the flow returns to step 3.

#### EF-04 — Invalid name
Triggered at step 6 when a supplied name is empty after trimming, or longer
than 60 characters.

1. The system changes nothing.
2. The system displays the corresponding message next to the name field
   ("Name is required" / "Name must be at most 60 characters"), and the flow
   returns to step 3.

#### EF-05 — Invalid description
Triggered at step 6 when the description is longer than 255 characters.

1. The system changes nothing.
2. The system displays "Description must be at most 255 characters" next to
   the description field, and the flow returns to step 3.

#### EF-06 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing, returns no account data and directs the user
   to authenticate.

#### EF-07 — Invalid balance
Triggered at step 6 when the balance is not a valid amount with at most two
decimal places.

1. The system changes nothing.
2. The system displays "Enter a valid amount" next to the balance field, and
   the flow returns to step 3.

### Acceptance criteria

- AC-01 — Sending `name=Savings` changes only the name; description and
  balance are unchanged.
- AC-02 — Sending the same request twice succeeds both times, with identical
  responses and an identical stored account. The second request does not
  fail EF-03 against the account's own name.
- AC-03 — Renaming "savings" to "Savings" on the same account succeeds.
- AC-04 — Renaming an account to the name of another account the same user
  owns fails with EF-03, and neither account changes.
- AC-05 — Sending an empty `description` clears it; leaving `description` out
  keeps it.
- AC-06 — A request with no fields succeeds and returns the account unchanged.
- AC-07 — A request containing `id` fails with EF-02, even when it also
  contains a valid `name`, and the name is not changed.
- AC-08 — Updating another user's account, a non-existent id, and an arbitrary
  string such as `abc` all produce the identical EF-01 response, and nothing
  changes.
- AC-09 — An unauthenticated request changes nothing.
- AC-10 — Sending `balance=-150.00` stores `-150.00`; sending it again leaves
  `-150.00`, not `-300.00`.
- AC-11 — Changing the balance shows the warning; cancelling it leaves the
  account unchanged, and acknowledging it applies the update.
- AC-12 — Changing only the name or description does not show the warning.
- AC-13 — `balance=12.345` fails with EF-07 and nothing changes.
- AC-14 — The edit form shows the current balance.
- AC-15 — Changing the balance from `800.00` to `850.00` makes it `850.00` at
  once, whatever months are open, pending or consolidated; no month total or
  closing changes.
- AC-16 — Sending `accounting_month` fails with EF-02.


## UC-05 — Delete a bank account

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-03, UC-10, Glossary

### Objective

Allow an authenticated user to delete one of their own bank accounts,
together with its credit cards, expenses and incomes, following the deletion
policy in the Glossary, after being warned of what will be erased and what
will be kept.

### Actors

- **User** (primary) — the account owner.
- **System** (secondary) — works out what will be deleted, asks for the
  required confirmations, and deletes.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the account no longer appears anywhere. Its credit
  cards are deleted as in UC-10. Its expenses, incomes and charges are
  deleted physically or logically according to their accounting month
  (Glossary, "Deleting a bank account or credit card").
- POST-02 — On failure or cancellation, nothing is deleted.

### Data

#### Request

| Parameter             | Type    | Required | Constraints                                                        |
|-----------------------|---------|----------|--------------------------------------------------------------------|
| `id`                  | text    | Yes      | Any string; identifies the account (as in UC-03)                   |
| `authorize_open_past` | boolean | When needed | Must be `true` when the account has entries in earlier open months |

### Functional requirements

- FR-01 — The system deletes an account only for the user who owns it.
- FR-02 — Deleting an account deletes every credit card associated with it,
  each following UC-10, and every Pix paid from its balance and every
  income received into it.
- FR-03 — Each expense, income and charge is handled according to its
  accounting month:
  - current or future month — deleted physically;
  - earlier month still open — deleted physically, only with the user's
    explicit authorization (FR-05);
  - consolidated month — removed logically: kept, never shown again, and the
    consolidated closings keep the amounts they recorded.
- FR-04 — Before deleting, the system shows a warning stating that the action
  cannot be undone and listing what will happen: how many credit cards,
  expenses and incomes will be erased, and which consolidated months will
  keep their records.
- FR-05 — When the account, or one of its credit cards, has entries in
  earlier accounting months that are still open, the system names those
  months and asks for an explicit, separate authorization to erase them.
  Without it, nothing is deleted.
- FR-06 — The account's own record is removed logically when it has entries
  in a consolidated month, so that those closings can still refer to it, and
  deleted physically otherwise. Either way, it never appears again.
- FR-07 — After the deletion, a name used by the deleted account may be used
  again (UC-01 FR-03).
- FR-08 — Any id that does not match an account the user owns produces the
  same "not found" response as UC-03 FR-02.
- FR-09 — Deleting is started from the account's details (UC-03).
- FR-10 — An internal transfer to or from the account (UC-16 FR-14) is
  deleted on both sides, following FR-03 for its accounting month.

### Main flow

1. On the account's details (UC-03), the user chooses to delete the account.
2. The system works out what the deletion involves (FR-02, FR-03) and shows
   the warning (FR-04).
   *(AF-01 if there are entries in earlier open months.)*
3. The user confirms.
   *(AF-02 if the user cancels.)*
4. The system retrieves the account with that id, restricted to accounts the
   user owns.
   *(EF-01 if no such account is found.)*
5. The system deletes the account, its cards, expenses, incomes and charges
   as in FR-02, FR-03 and FR-06, and confirms: "Bank account <name>
   deleted".
6. The user returns to the bank accounts list (UC-02).

### Alternate flows

#### AF-01 — Authorization for earlier open months
Triggered at step 2 when the account or its cards have entries in earlier
accounting months that are still open.

1. The system names those months and the entries in them, and asks for an
   explicit authorization to erase them (FR-05).
2. If the user authorizes it, the flow continues at step 3.
3. If the user does not, nothing is deleted and the user stays on the
   account's details.

#### AF-02 — Deletion cancelled
Triggered at step 3 when the user does not confirm.

1. Nothing is deleted, and the user stays on the account's details.

### Exception flows

#### EF-01 — Account not found
Triggered at step 4 when no account the user owns has that id.

1. The system deletes nothing.
2. The system displays "Bank account not found" and offers a way back to the
   bank accounts list, and the flow ends.

#### EF-02 — Authorization missing
Triggered at step 4 when authorization is needed (FR-05) and
`authorize_open_past` is not `true`.

1. The system deletes nothing.
2. The system rejects the request, naming the earlier open months, and the
   flow returns to step 2.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system deletes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Deleting an account shows the warning, with the number of cards,
  expenses and incomes involved, before anything is deleted; cancelling
  leaves everything unchanged.
- AC-02 — With August consolidated and September current: deleting an
  account with a Pix of August, a Pix of September and an income of October
  removes the August Pix logically, erases the September Pix and the October
  income, and the August closing keeps its amounts.
- AC-03 — With August still open in September, deleting the same account
  asks for explicit authorization naming August; without it, nothing is
  deleted.
- AC-04 — Deleting an account also deletes its credit cards, each following
  UC-10.
- AC-05 — After the deletion, the account appears in no list, search or
  month view, and a new account can be created with the same name.
- AC-06 — Deleting another user's account, a non-existent id, and an
  arbitrary string such as `abc` all produce the identical EF-01 response,
  and nothing is deleted.
- AC-07 — An unauthenticated request deletes nothing.

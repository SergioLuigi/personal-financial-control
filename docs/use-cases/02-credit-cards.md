# Credit cards — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-06 to UC-10 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-06 — Create a credit card](#uc-06--create-a-credit-card)
- [UC-07 — View credit card details](#uc-07--view-credit-card-details)
- [UC-08 — Update a credit card](#uc-08--update-a-credit-card)
- [UC-09 — List credit cards](#uc-09--list-credit-cards)
- [UC-10 — Delete a credit card](#uc-10--delete-a-credit-card)


## UC-06 — Create a credit card

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-03, UC-07

### Objective

Allow an authenticated user to register a new credit card and associate it
with one of their own bank accounts, so that card purchases can later be
tracked against its limit and due date.

### Actors

- **User** (primary) — the owner of the card and of the bank account.
- **System** (secondary) — validates the card, enforces ownership of the bank
  account and persists the card.

### Pre-conditions

- PRE-01 — The user is authenticated.
- PRE-02 — The user owns at least one bank account.

### Post-conditions

- POST-01 — On success, a credit card is persisted, owned by the requesting
  user and associated with the chosen bank account, with a balance equal to
  its limit.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field             | Type          | Required | Constraints                                          |
|-------------------|---------------|----------|------------------------------------------------------|
| `name`            | text          | Yes      | 1–60 chars, trimmed, unique per user                 |
| `description`     | text          | No       | 0–255 chars                                          |
| `bank_account_id` | text          | Yes      | Must match a bank account the user owns (UC-03)      |
| `due_day`         | integer       | Yes      | Day of the month the bill is due, 1–31               |
| `limit`           | decimal(13,2) | Yes      | Greater than `0.00`                                  |
| `balance`         | decimal(13,2) | —        | Calculated, never supplied: the available limit (FR-09) |

### Functional requirements

- FR-01 — The system persists a new credit card owned by the requesting user.
- FR-02 — A credit card has a name, an optional description, a bank account,
  a due day, a limit and a balance.
- FR-03 — A credit card is associated with exactly one bank account, and that
  account must be owned by the requesting user. A bank account may have any
  number of credit cards. The card's bills are always paid from that account
  (UC-30), and what is owed on them lowers its projected balance
  (Glossary).
- FR-04 — Card names are unique per user, compared case-insensitively and
  after trimming surrounding whitespace, following the same rule as bank
  account names (UC-01 FR-03).
- FR-05 — The due day is a day of the month, repeated every month. In a month
  shorter than the due day, the bill is due on that month's last day (a card
  due on day 31 is due on 30 April and on 28 or 29 February).
- FR-06 — The limit is a positive amount with at most two decimal places.
- FR-07 — Creating a credit card is started from the details of the bank
  account it will belong to (UC-03), which is preselected.
- FR-08 — Any `bank_account_id` that does not match an account the user owns
  produces the same "not found" response as UC-03 FR-02.
- FR-09 — The balance is the card's available limit (Glossary): the limit
  minus what is owed — the full value of its purchases, counted as defined in
  UC-15 FR-06, minus its refunds (UC-32) and bill payments (UC-30). It lets the user see how much of the limit is still
  available, and how much they owe (limit − balance).
- FR-10 — The balance is calculated by the system and cannot be supplied by
  the user. A new card has no expenses, so its balance equals its limit.
- FR-11 — The balance may become negative when expenses exceed the limit.

### Main flow

1. On a bank account's details (UC-03), the user chooses to add a credit card.
2. The system presents an empty credit-card form with that bank account
   preselected.
3. The user supplies a name, a due day and a limit, and optionally a
   description.
4. The user confirms the creation.
5. The system retrieves the bank account, restricted to accounts the user
   owns.
   *(EF-01 if no such account is found.)*
6. The system validates the submitted data.
   *(EF-02 if the name is already taken; EF-03 if the name is missing or too
   long; EF-04 if the due day is invalid; EF-05 if the limit is invalid;
   EF-07 if a balance is supplied.)*
7. The system persists the card, associated with the bank account, with its
   balance set to its limit (FR-10), and confirms: "Credit card <name>
   created".

### Exception flows

#### EF-01 — Bank account not found
Triggered at step 5 when no account the user owns has that id — because no
account has it, the account has been deleted, or another user owns it.

1. The system does not persist the card.
2. The system displays "Bank account not found" and offers a way back to the
   bank accounts list, and the flow ends.

#### EF-02 — Name already taken
Triggered at step 6 when the user already owns a credit card with that name.

1. The system does not persist the card.
2. The system displays "Credit card name already exists" next to the name
   field, and the flow returns to step 3.

#### EF-03 — Invalid name
Triggered at step 6 when the name is empty after trimming, or longer than
60 characters.

1. The system does not persist the card.
2. The system displays the corresponding message next to the name field
   ("Name is required" / "Name must be at most 60 characters"), and the flow
   returns to step 3.

#### EF-04 — Invalid due day
Triggered at step 6 when the due day is missing, not an integer, or outside
1–31.

1. The system does not persist the card.
2. The system displays "Due day must be between 1 and 31" next to the due day
   field, and the flow returns to step 3.

#### EF-05 — Invalid limit
Triggered at step 6 when the limit is missing, not a valid amount with at
most two decimal places, or not greater than `0.00`.

1. The system does not persist the card.
2. The system displays "Limit must be a positive amount" next to the limit
   field, and the flow returns to step 3.

#### EF-06 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

#### EF-07 — Balance supplied
Triggered at step 6 when the request contains a `balance`.

1. The system does not persist the card.
2. The system rejects the request with "balance is calculated and cannot be
   set", and the flow ends.

### Acceptance criteria

- AC-01 — Creating a card with a name, due day `10` and limit `5000.00` on one
  of the user's bank accounts succeeds, and the card is associated with that
  account.
- AC-02 — Creating a card without a description succeeds.
- AC-03 — Two cards can be associated with the same bank account.
- AC-04 — A second card with the same name for the same user fails with the
  EF-02 message, even on a different bank account; "visa" and " Visa "
  collide with an existing "Visa".
- AC-05 — Two different users can each own a card named "Visa".
- AC-06 — Using another user's bank account, a non-existent id, or an
  arbitrary string such as `abc` as `bank_account_id` produces the identical
  EF-01 response, and no card is persisted.
- AC-07 — Due days `0`, `32` and `10.5` each fail with EF-04; `1` and `31`
  succeed.
- AC-08 — Limits `0.00`, `-100.00` and `100.001` each fail with EF-05;
  `0.01` succeeds.
- AC-09 — A card with due day `31` is due on 30 April and on the last day of
  February.
- AC-10 — An unauthenticated request persists nothing.
- AC-11 — A card created with limit `5000.00` has balance `5000.00`.
- AC-12 — A request containing `balance` fails with EF-07, even when every
  other field is valid, and no card is persisted.
- AC-13 — With limit `5000.00` and expenses totalling `1200.00`, the balance
  is `3800.00`; with expenses totalling `5300.00`, it is `-300.00`.
- AC-14 — With limit `5000.00`, purchases of `1200.00` and a bill payment of
  `700.00`, the balance is `4500.00`.


## UC-07 — View credit card details

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-03, UC-06, UC-08, UC-09, UC-10, UC-15, UC-16, UC-17, UC-18, UC-39

### Objective

Allow an authenticated user to see the full details of one of their own
credit cards, including its balance, so they can check how much of the limit
they have used.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — retrieves the card, calculates its balance and
  enforces ownership.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter       | Type    | Required | Constraints                                   |
|-----------------|---------|----------|-----------------------------------------------|
| `id`            | text    | Yes      | Any string; no format is enforced             |
| `expenses_page` | integer | No       | ≥ 0, defaults to `0` (the first page)         |

#### Response

| Field          | Type          | Notes                                                                    |
|----------------|---------------|--------------------------------------------------------------------------|
| `id`           | text          | The card's id                                                            |
| `name`         | text          | As stored                                                                |
| `description`  | text          | Empty when none was supplied                                             |
| `bank_account` | object        | `id` and `name` of the associated bank account                           |
| `due_day`      | integer       | 1–31                                                                     |
| `limit`        | decimal(13,2) | As stored                                                                |
| `balance`      | decimal(13,2) | Available limit (UC-06 FR-09); may be negative                           |
| `bills`        | object        | The current open bill and the next bill due: `month`, `total`, `due_date`, `status`, `outstanding` |
| `expenses`     | page          | `items` (`id`, `date`, `value`, `recurring`, `installments`, `payment_method`, `recipient_name`, `status`; 0–10), `page`, `total_items`, `total_pages` |

### Functional requirements

- FR-01 — The system returns a card only to the user who owns it.
- FR-02 — Any id that does not match a card the user owns produces the same
  "not found" response, whether no card has that id or another user owns it.
  The user cannot learn whether another user's card exists.
- FR-03 — The balance is calculated at the moment of the request, so it
  reflects every expense, refund and bill payment registered up to then.
- FR-04 — The details are reached from its bank account's details (UC-03) or
  the credit cards list (UC-09), among other places such as the home summary
  (UC-38).
- FR-05 — From the details, the user can return to where they came from: the
  bank account's details (UC-03), or the credit cards list (UC-09) on the same
  page and with the same filters they left.
- FR-06 — The details list the card's expenses — card purchases (UC-15) and
  Pix on its limit (UC-16) — at most 10 per page,
  ordered by date descending with `id` as the final tie-breaker. Paging
  follows the same rules as UC-09 FR-04 and FR-10; a card with no expenses
  shows an empty list, not an error. Removed expenses never appear; stopped
  ones appear, marked as stopped (Glossary).

### Main flow

1. On a bank account's details (UC-03) or on the credit cards list (UC-09),
   the user selects a credit card.
2. The system retrieves the card with that id, restricted to cards the user
   owns.
   *(EF-01 if no such card is found.)*
3. The system calculates the card's balance (FR-03).
   *(EF-03 if `expenses_page` is negative or not an integer.)*
4. The system displays the card's name, description, bank account, due day,
   limit and balance, and the requested page of its expenses (FR-06).
5. The user returns to where they came from (FR-05).
6. Alternatively, the user may move to another page of expenses, returning
   the flow to step 2; edit the card, leading to UC-08; add an expense to it,
   leading to UC-15; register a Pix on its limit, leading to UC-16; schedule a
   Pix on its limit, leading to UC-17; select one of its expenses, leading
   to UC-18; import a statement, leading to UC-39; open a bill, leading to
   UC-29; pay a bill, leading to UC-30; register a refund,
   leading to UC-32; or delete the card, leading to UC-10.

### Exception flows

#### EF-01 — Credit card not found
Triggered at step 2 when no card the user owns has that id — because no card
has it, the card has been deleted, or another user owns it.

1. The system returns no card data.
2. The system displays "Credit card not found" and offers a way back to where
   the user came from — the bank account's details or the credit cards list —
   and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no card data whatsoever and directs the user to
   authenticate.

#### EF-03 — Invalid expenses page
Triggered at step 3 when `expenses_page` is negative or not an integer.

1. The system does not return the details.
2. The system rejects the request with "Page must be a non-negative integer",
   and the flow ends.

### Acceptance criteria

- AC-01 — Opening one of the user's own cards shows its id, name,
  description, bank account, due day, limit and balance exactly as stored or
  calculated.
- AC-02 — A card with limit `5000.00` and expenses totalling `1200.00` shows
  balance `3800.00`.
- AC-03 — A card with no expenses shows a balance equal to its limit.
- AC-04 — Requesting another user's card, a non-existent id, and an arbitrary
  string such as `abc` all produce the identical EF-01 response, never a
  system error.
- AC-05 — An unauthenticated request returns no card data.
- AC-06 — A card with 25 expenses shows 10 on `expenses_page` 0, most recent
  first, with `total_items` 25 and `total_pages` 3; paging through visits
  every expense exactly once.
- AC-07 — A card with no expenses shows an empty expenses list.
- AC-08 — The details show the current open bill and the next bill due, with
  their totals, due dates and statuses.


## UC-08 — Update a credit card

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-06, UC-07

### Objective

Allow an authenticated user to change the name, description, due day and
limit of one of their own credit cards through a partial update that is
idempotent: sending the same request once or many times leaves the card in
the same state.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — validates the changes, enforces ownership and
  persists the card.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the supplied fields hold the new values, every other
  field is unchanged, and the balance reflects the new limit.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

#### Request

| Parameter     | Type          | Required | Constraints                                        |
|---------------|---------------|----------|----------------------------------------------------|
| `id`          | text          | Yes      | Any string; identifies the card (as in UC-07)      |
| `name`        | text          | No       | 1–60 chars, trimmed, unique per user               |
| `description` | text          | No       | 0–255 chars; an empty value clears the description |
| `due_day`     | integer       | No       | 1–31                                               |
| `limit`       | decimal(13,2) | No       | Greater than `0.00`                                |

Only `name`, `description`, `due_day` and `limit` may be supplied. `id`,
`bank_account_id` and `balance` cannot be changed through this use case.

#### Response

The updated card, with the same fields as UC-07.

### Functional requirements

- FR-01 — The system updates a card only for the user who owns it.
- FR-02 — The update is partial: a field left out of the request keeps its
  current value.
- FR-03 — Every supplied field carries the absolute new value, never a
  relative change. Repeating the same request therefore produces the same
  card and the same response as sending it once.
- FR-04 — Name uniqueness follows UC-06 FR-04, excluding the card being
  updated. Keeping the current name, or changing only its capitalization, is
  allowed.
- FR-05 — A request that changes nothing — no fields supplied, or values equal
  to the current ones — succeeds and returns the card unchanged.
- FR-06 — A request containing a field other than `name`, `description`,
  `due_day` or `limit` — such as `balance`, `bank_account_id` or `id` — is
  rejected as a whole; nothing is changed.
- FR-07 — Any id that does not match a card the user owns produces the same
  "not found" response as UC-07 FR-02.
- FR-08 — The update is started from the card's details (UC-07).
- FR-09 — After a limit change, the balance is recalculated as the new
  limit's available limit (UC-06 FR-09). It may become negative (UC-06
  FR-11).
- FR-10 — When the user changes the limit, the system displays a warning
  about the consequences before the update is sent, and the update proceeds
  only if the user acknowledges it. The warning is not shown when the limit
  is left unchanged. The warning reads:

  > The limit of [card] will change from [A] to [B]. From now on, the
  > available limit will be [C].
  > Expenses and bills already registered do not change.
  > *(if the available limit becomes negative)* Your open purchases exceed the
  > new limit: the available limit will be negative by [D].
- FR-11 — When the user changes the due day, the system displays a warning
  about the consequences before the update is sent, and the update proceeds
  only if the user acknowledges it. The warning is not shown when the due day
  is left unchanged. The warning reads:

  > The due day of [card] will change from day [A] to day [B].
  > Every unpaid bill, from the [month] bill on, will be due on day [B]. Bills
  > already paid keep their original due date.
  > *(if a bill becomes overdue with the new date)* With the new date, the
  > [month] bill will be overdue at once, and what is unpaid on it will be
  > carried to the next bill.
- FR-12 — When both the limit and the due day change, both warnings are shown
  together and a single acknowledgement covers both.
- FR-13 — The new due day applies to every unpaid bill, including one whose
  new due date has already passed: such a bill becomes overdue at once, and
  what is unpaid on it is carried to the next bill (UC-43). The due
  day warning states it (FR-11).

### Main flow

1. On the card's details (UC-07), the user chooses to edit the card.
2. The system presents a form pre-filled with the current name, description,
   due day and limit.
3. The user changes any of the name, the description, the due day and the
   limit.
4. The user confirms the update.
   *(AF-01 if the limit or the due day was changed.)*
5. The system retrieves the card with that id, restricted to cards the user
   owns.
   *(EF-01 if no such card is found.)*
6. The system validates the supplied fields.
   *(EF-02 if an unchangeable field is supplied; EF-03 if the name is taken;
   EF-04 if the name is missing or too long; EF-05 if the description is too
   long; EF-06 if the due day is invalid; EF-07 if the limit is invalid.)*
7. The system persists the supplied fields, leaving the others untouched,
   recalculates the balance (FR-09), and confirms: "Credit card <name>
   updated".
8. The user returns to the card's details (UC-07), which show the new values.

### Alternate flows

#### AF-01 — Limit or due day change warning
Triggered at step 4 when the limit, the due day, or both differ from the
current ones.

1. Before sending the update, the system displays the warning for each
   changed field: the limit warning (FR-10), the due day warning (FR-11), or
   both together (FR-12).
2. If the user acknowledges, the flow continues at step 5.
3. If the user cancels, nothing is sent or changed, and the flow returns to
   step 3 with the entered values kept.

### Exception flows

#### EF-01 — Credit card not found
Triggered at step 5 when no card the user owns has that id.

1. The system changes nothing and returns no card data.
2. The system displays "Credit card not found" and offers a way back to the
   credit cards list, and the flow ends.

#### EF-02 — Unchangeable field
Triggered at step 6 when the request contains a field other than `name`,
`description`, `due_day` or `limit`.

1. The system changes nothing.
2. The system rejects the request, naming the field ("balance cannot be
   changed"), and the flow ends.

#### EF-03 — Name already taken
Triggered at step 6 when another card of the same user already has that name.

1. The system changes nothing.
2. The system displays "Credit card name already exists" next to the name
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

#### EF-06 — Invalid due day
Triggered at step 6 when a supplied due day is not an integer or is outside
1–31.

1. The system changes nothing.
2. The system displays "Due day must be between 1 and 31" next to the due day
   field, and the flow returns to step 3.

#### EF-07 — Invalid limit
Triggered at step 6 when a supplied limit is not a valid amount with at most
two decimal places, or is not greater than `0.00`.

1. The system changes nothing.
2. The system displays "Limit must be a positive amount" next to the limit
   field, and the flow returns to step 3.

#### EF-08 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing, returns no card data and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — Sending `name=Visa Gold` changes only the name; every other field
  is unchanged.
- AC-02 — Sending the same request twice succeeds both times, with identical
  responses and an identical stored card. The second request does not fail
  EF-03 against the card's own name.
- AC-03 — Sending `limit=6000.00` twice leaves the limit at `6000.00`, not
  `12000.00`.
- AC-04 — With expenses totalling `1200.00`, changing the limit from
  `5000.00` to `6000.00` changes the balance from `3800.00` to `4800.00`;
  changing it to `1000.00` makes the balance `-200.00`.
- AC-05 — Changing the limit shows the limit warning; changing the due day
  shows the due day warning; changing both shows both with a single
  acknowledgement.
- AC-06 — Cancelling a warning leaves the card unchanged; acknowledging it
  applies the update.
- AC-07 — Changing only the name or description shows no warning, and
  neither does resending the current limit or due day.
- AC-08 — A request containing `balance` or `bank_account_id` fails with
  EF-02, even when it also contains a valid `name`, and the name is not
  changed.
- AC-09 — Due days `0` and `32`, and limits `0.00` and `-100.00`, each fail
  with the matching exception and nothing changes.
- AC-10 — Updating another user's card, a non-existent id, and an arbitrary
  string such as `abc` all produce the identical EF-01 response, and nothing
  changes.
- AC-11 — An unauthenticated request changes nothing.
- AC-12 — On 12 September, changing the due day from 15 to 10 makes the
  unpaid September bill overdue at once; the warning names it and says its
  unpaid amount will be carried to the next bill.
- AC-13 — The limit warning shows the new available limit, and the negative
  amount when purchases exceed the new limit.


## UC-09 — List credit cards

**Status:** Draft · **Last updated:** 2026-09-25 · **Related:** UC-02, UC-07

### Objective

Allow an authenticated user to browse all their own credit cards, across
every bank account, ten at a time, from a dedicated credit cards menu, so
they can find one to inspect or edit.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — filters, orders and paginates the cards.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter         | Type          | Required | Constraints                                    |
|-------------------|---------------|----------|------------------------------------------------|
| `page`            | integer       | No       | ≥ 0, defaults to `0` (the first page)          |
| `name`            | text          | No       | 0–60 chars, trimmed                            |
| `description`     | text          | No       | 0–255 chars, trimmed                           |
| `bank_account_id` | text          | No       | Chosen from the user's own bank accounts       |
| `due_day`         | integer       | No       | 1–31                                           |
| `min_limit`       | decimal(13,2) | No       | Inclusive lower bound                          |
| `max_limit`       | decimal(13,2) | No       | Inclusive upper bound; must be ≥ `min_limit`   |
| `min_balance`     | decimal(13,2) | No       | Inclusive lower bound; may be negative         |
| `max_balance`     | decimal(13,2) | No       | Inclusive upper bound; must be ≥ `min_balance` |

#### Response

| Field         | Type          | Notes                                                                        |
|---------------|---------------|------------------------------------------------------------------------------|
| `items`       | array of card | `id`, `name`, `description`, `bank_account` (`id`, `name`), `due_day`, `limit`, `balance`; 0–10 |
| `page`        | integer       | Echoes the page returned                                                     |
| `total_items` | integer       | Cards matching the filters, across all pages                                 |
| `total_pages` | integer       | `ceil(total_items / 10)`, minimum `1`                                        |

### Functional requirements

- FR-01 — The page contains only cards owned by the requesting user.
- FR-02 — A page contains at most 10 cards.
- FR-03 — Cards are ordered by name ascending, with `id` as the final
  tie-breaker, so that ordering is stable: no card appears on two pages or is
  skipped between them.
- FR-04 — Pages are numbered from 0. The system reports `total_pages` so the
  user can move backwards, move forwards, or jump straight to a page number.
- FR-05 — The user may filter by every card field except `id`:
  - name and description match case-insensitively as substrings, after
    trimming;
  - the bank account matches the chosen account exactly;
  - the due day matches exactly;
  - the monetary fields, limit and balance, are filtered by ranges that are
    inclusive at both ends. Either bound may be left out.
- FR-06 — Filters combine with AND: a card is returned only when it satisfies
  every filter supplied.
- FR-07 — The balance filter uses the balance calculated at the moment of the
  request (UC-07 FR-03).
- FR-08 — A `bank_account_id` that does not match an account the user owns
  returns an empty page, the same as an account with no cards. It is not an
  error, and the user cannot learn whether another user's account exists.
- FR-09 — `total_items` and `total_pages` reflect the active filters, not the
  user's whole collection.
- FR-10 — A page beyond the last one returns an empty `items` array with
  correct metadata. It is a valid result, not an error.
- FR-11 — A user with no cards receives an empty page. This is a valid result,
  not an error.
- FR-12 — The list is reached from its own credit cards menu, independently
  of the bank accounts menu.

### Main flow

1. The user opens the credit cards list from the credit cards menu.
2. The system applies the supplied page number and filters, falling back to
   page 0 and no filters for any that are absent.
3. The system validates the parameters.
   *(EF-01 if any parameter is invalid.)*
4. The system retrieves the user's matching cards, ordered per FR-03 and
   windowed to the requested page, with each card's balance calculated.
5. The system returns the page together with `total_items` and `total_pages`,
   and the user sees the cards.
   *(AF-01 if the page is empty.)*
6. The user may move to another page or change the filters, returning the flow
   to step 2.
7. The user may select a card on the page to see its details, leading to
   UC-07.

### Alternate flows

#### AF-01 — Empty result
Triggered at step 5 when `items` is empty.

1. If the user owns no cards at all, the system says so ("No credit cards
   yet") and explains that cards are added from a bank account's details
   (UC-06).
2. If the emptiness is caused by the filters, the system says so ("No credit
   cards match your search") and offers to clear them.

### Exception flows

#### EF-01 — Invalid parameters
Triggered at step 3 when `page` is negative or not an integer, when `due_day`
is not an integer from 1 to 31, when a monetary bound is not a valid amount,
or when a maximum is below its minimum.

1. The system does not return a page.
2. The system rejects the request, naming the offending parameter ("Maximum
   limit must not be lower than minimum limit"), and the flow ends.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no card data whatsoever and directs the user to
   authenticate.

### Acceptance criteria

- AC-01 — A user owning 25 cards across several bank accounts and requesting
  no page receives 10 items, `page` 0, `total_items` 25, `total_pages` 3.
- AC-02 — Paging from 0 to the last page visits every card exactly once.
- AC-03 — Page 3 of that set returns an empty `items` array with
  `total_items` 25 — not an error.
- AC-04 — A user owning no cards receives an empty `items` array,
  `total_items` 0, `total_pages` 1.
- AC-05 — Two users each owning cards see only their own, and neither user's
  `total_items` includes the other's.
- AC-06 — `name=vis` returns "Visa" and "VISA GOLD" but not "Mastercard".
- AC-07 — `bank_account_id` set to one of the user's accounts returns only
  that account's cards; set to another user's account, a non-existent id, or
  `abc`, it returns an empty page, not an error.
- AC-08 — `due_day=10` returns only cards due on day 10.
- AC-09 — `min_limit=1000&max_limit=5000` returns cards with limits from
  `1000.00` to `5000.00` inclusive.
- AC-10 — `max_balance=0` returns only cards whose calculated balance is zero
  or negative.
- AC-11 — `name=vis&due_day=10&min_limit=1000` returns only the cards
  satisfying all three filters.
- AC-12 — `page=-1`, `due_day=32`, and a `max_balance` below `min_balance`
  are each rejected with EF-01.
- AC-13 — Opening a card from page 2 of a filtered list and returning lands on
  page 2 with the same filters.


## UC-10 — Delete a credit card

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-05, UC-07, Glossary

### Objective

Allow an authenticated user to delete one of their own credit cards, together
with its expenses, following the deletion policy in the Glossary, after being
warned of what will be erased and what will be kept.

### Actors

- **User** (primary) — the card owner.
- **System** (secondary) — works out what will be deleted, asks for the
  required confirmations, and deletes.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the card no longer appears anywhere, and its expenses
  and charges are deleted physically or logically according to their
  accounting month (Glossary, "Deleting a bank account or credit card").
- POST-02 — On failure or cancellation, nothing is deleted.

### Data

#### Request

| Parameter             | Type    | Required    | Constraints                                                     |
|-----------------------|---------|-------------|-----------------------------------------------------------------|
| `id`                  | text    | Yes         | Any string; identifies the card (as in UC-07)                   |
| `authorize_open_past` | boolean | When needed | Must be `true` when the card has charges in earlier open months |

### Functional requirements

- FR-01 — The system deletes a card only for the user who owns it.
- FR-02 — Deleting a card deletes every expense funded by it — card purchases
  and Pix on its limit — charge by charge, according to each charge's
  accounting month:
  - current or future month — deleted physically;
  - earlier month still open — deleted physically, only with the user's
    explicit authorization (FR-04);
  - consolidated month — removed logically: kept, never shown again, and the
    consolidated closings keep the amounts they recorded.
  An expense in installments or a recurring expense may therefore have some
  charges erased and others kept.
- FR-03 — Before deleting, the system shows a warning stating that the action
  cannot be undone and listing what will happen: how many expenses and
  charges will be erased, which consolidated months will keep their records,
  and — when bill payments are erased — how much the bank account's balance
  will rise.
- FR-04 — When the card has charges in earlier accounting months that are
  still open, the system names those months and asks for an explicit,
  separate authorization to erase them. Without it, nothing is deleted.
- FR-05 — The card's own record is removed logically when it has charges in a
  consolidated month, and deleted physically otherwise. Either way, it never
  appears again, and its name may be used again (UC-06 FR-04).
- FR-06 — Any id that does not match a card the user owns produces the same
  "not found" response as UC-07 FR-02.
- FR-07 — Deleting is started from the card's details (UC-07), or as part of
  deleting its bank account (UC-05).
- FR-08 — The card's refunds and bill payments follow FR-02 for their
  accounting month, like its expenses. Erasing a bill payment gives its
  amount back to the bank account: the account's balance and projected
  balance are recalculated.

### Main flow

1. On the card's details (UC-07), the user chooses to delete the card.
2. The system works out what the deletion involves (FR-02) and shows the
   warning (FR-03).
   *(AF-01 if there are charges in earlier open months.)*
3. The user confirms.
   *(AF-02 if the user cancels.)*
4. The system retrieves the card with that id, restricted to cards the user
   owns.
   *(EF-01 if no such card is found.)*
5. The system deletes the card and its expenses and charges as in FR-02 and
   FR-05, and confirms: "Credit card <name> deleted".
6. The user returns to the bank account's details (UC-03).

### Alternate flows

#### AF-01 — Authorization for earlier open months
Triggered at step 2 when the card has charges in earlier accounting months
that are still open.

1. The system names those months and the charges in them, and asks for an
   explicit authorization to erase them (FR-04).
2. If the user authorizes it, the flow continues at step 3.
3. If the user does not, nothing is deleted and the user stays on the card's
   details.

#### AF-02 — Deletion cancelled
Triggered at step 3 when the user does not confirm.

1. Nothing is deleted, and the user stays on the card's details.

### Exception flows

#### EF-01 — Credit card not found
Triggered at step 4 when no card the user owns has that id.

1. The system deletes nothing.
2. The system displays "Credit card not found" and offers a way back to the
   credit cards list, and the flow ends.

#### EF-02 — Authorization missing
Triggered at step 4 when authorization is needed (FR-04) and
`authorize_open_past` is not `true`.

1. The system deletes nothing.
2. The system rejects the request, naming the earlier open months, and the
   flow returns to step 2.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system deletes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Deleting a card shows the warning before anything is deleted;
  cancelling leaves everything unchanged.
- AC-02 — With January to August consolidated and September current, a
  purchase in 12 installments from May: the May to August installments are
  removed logically and kept in their closings, and the September to April
  installments are erased.
- AC-03 — A recurring charge on the card has its consolidated months removed
  logically and its current and future months erased.
- AC-04 — With August still open in September, deleting a card with an
  August purchase asks for explicit authorization naming August; without it,
  nothing is deleted.
- AC-05 — After the deletion, the card and its expenses appear in no list,
  search or month view, and a new card can be created with the same name.
- AC-06 — Deleting another user's card, a non-existent id, and an arbitrary
  string such as `abc` all produce the identical EF-01 response, and nothing
  is deleted.
- AC-07 — An unauthenticated request deletes nothing.
- AC-08 — Deleting, with authorization, a card whose September bill was paid
  with `350.00` in September erases that payment and raises its bank
  account's balance by `350.00`; the warning states it.

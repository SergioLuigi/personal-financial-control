# Categories — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-11 to UC-14 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-11 — Create a category](#uc-11--create-a-category)
- [UC-12 — List categories](#uc-12--list-categories)
- [UC-13 — Update a category](#uc-13--update-a-category)
- [UC-14 — Delete a category](#uc-14--delete-a-category)


## UC-11 — Create a category

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-12, UC-13, UC-14, UC-15, UC-16, Glossary

### Objective

Allow an authenticated user to create a category of their own — for expenses
or for incomes — in addition to the default lists every user starts with, so
that expenses can be classified by what they were for and incomes by where
they came from.

### Actors

- **User** (primary) — the owner of the categories.
- **System** (secondary) — validates and persists the category, and provides
  the default list.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the category exists and can be chosen for any
  expense.
- POST-02 — On failure, nothing is persisted and the user keeps the data
  already entered.

### Data

| Field         | Type | Required | Constraints                                |
|---------------|------|----------|--------------------------------------------|
| `kind`        | text | Yes      | `expense` or `income`                      |
| `name`        | text | Yes      | 1–40 chars, trimmed, unique per user and kind |
| `description` | text | No       | 0–255 chars                                |

### Functional requirements

- FR-01 — The system persists a new category owned by the requesting user.
- FR-02 — Category names are unique per user and kind, compared
  case-insensitively after trimming, as bank account names (UC-01 FR-03).
- FR-03 — A category of kind `expense` classifies expenses of every kind,
  source and payment method (UC-15 FR-02); a category of kind `income`
  classifies incomes (UC-24). An expense can only use an expense category,
  and an income only an income category.
- FR-04 — Creating a category is started from the categories list
  (UC-12), or from the category field of an expense or income form, returning
  to it with the new category, of that form's kind, chosen.
- FR-05 — Every user starts with the default categories below, so that no
  category needs to be created before registering expenses or incomes.
  Default categories are fixed: they cannot be renamed, changed or deleted
  (UC-13, UC-14). Only the categories the user creates can be.

  Expense categories:

  | Name          |
  |---------------|
  | Food & dining |
  | Groceries     |
  | Housing       |
  | Utilities     |
  | Transport     |
  | Health        |
  | Education     |
  | Leisure       |
  | Subscriptions |
  | Clothing      |
  | Travel        |
  | Taxes & fees  |
  | Other         |

  Income categories:

  | Name          |
  |---------------|
  | Salary        |
  | Freelance     |
  | Investments   |
  | Refunds       |
  | Gifts         |
  | Other income  |
- FR-06 — The fixed "Internal transfer" category (UC-16 FR-14) is managed by
  the system: it is never offered to the user — it is not listed among the
  user's categories and cannot be chosen, created, renamed or deleted. The
  system assigns it to internal transfers only.

### Main flow

1. On the categories list (UC-12), or from an expense or income form, the
   user chooses to create a category.
2. The system presents an empty category form.
3. The user chooses the kind — preselected when coming from an expense or
   income form — supplies a name and optionally a description, and
   confirms.
4. The system validates them.
   *(EF-01 if the name is already taken; EF-02 if the name, description or
   kind is invalid.)*
5. The system persists the category and confirms: "Category <name>
   created".
6. The user returns to the categories list, or to the expense or income form
   with the new category chosen.

### Exception flows

#### EF-01 — Name already taken
Triggered at step 4 when the user already has a category of the same kind
with that name, including a default one.

1. The system does not persist the category.
2. The system displays "Category name already exists", and the flow returns
   to step 3.

#### EF-02 — Invalid name or description
Triggered at step 4 when the name is empty after trimming or longer than 40
characters, the description is longer than 255 characters, or the kind is
not `expense` or `income`.

1. The system does not persist the category.
2. The system displays the corresponding message next to the field, and the
   flow returns to step 3.

#### EF-03 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system persists nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — A new user has the 13 default expense categories and the 6 default
  income categories without creating any.
- AC-02 — Creating the expense category "Pets" succeeds; creating "pets" or
  " Groceries " as expense categories fails with EF-01; creating an income
  category "Pets" succeeds.
- AC-03 — Creating a category from an expense form returns to the form with
  it chosen.
- AC-04 — "Internal transfer" cannot be created, chosen or found among the
  user's categories.
- AC-05 — Two users may each have a category named "Pets".
- AC-06 — Creating a category from an income form creates an income
  category and returns to the form with it chosen.


## UC-12 — List categories

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-11, UC-13, UC-14, UC-20

### Objective

Allow an authenticated user to see all their categories, find one, and see
how much it is used.

### Actors

- **User** (primary) — the owner of the categories.
- **System** (secondary) — lists and filters the categories.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — No data is modified. This use case is read-only.

### Data

#### Request

| Parameter | Type    | Required | Constraints                           |
|-----------|---------|----------|---------------------------------------|
| `page`    | integer | No       | ≥ 0, defaults to `0`                  |
| `name`    | text    | No       | 0–40 chars, trimmed                   |
| `kind`    | text    | No       | `expense` or `income`; omitted means both |

#### Response

A page of categories — `id`, `kind`, `name`, `description`, `default` (true
for the fixed default categories), `entry_count` (expenses or incomes using
it, active and stopped), `current_month_total` (spent or received in it in
the current accounting month) — with `page`, `total_items` and
`total_pages`.

### Functional requirements

- FR-01 — The list contains only the requesting user's categories, default
  and created alike, 10 per page, ordered by name with `id` as the final
  tie-breaker.
- FR-02 — The name filter matches case-insensitively as a substring; the
  kind filter matches exactly.
- FR-03 — Each category shows how many expenses or incomes use it and how
  much was spent or received in it in the current accounting month. Default
  categories are marked as such, and offer no edit or delete action.
- FR-04 — The list is reached from its own categories menu. From it the user
  can create (UC-11), edit (UC-13) or delete (UC-14) a
  category of their own, or open the expenses list (UC-20) or the incomes
  list (UC-27) filtered by that category.
- FR-05 — The fixed "Internal transfer" category is not listed.

### Main flow

1. The user opens the categories list.
2. The system validates the parameters.
   *(EF-01 if any is invalid.)*
3. The system returns the requested page.
4. The user may change page or filter, returning to step 2; create a category
   (UC-11); edit or delete one of their own (UC-13, UC-14); or see a
   category's entries in the expenses or incomes list (UC-20, UC-27).

### Exception flows

#### EF-01 — Invalid parameters
Triggered at step 2 when `page` is negative or not an integer.

1. The system does not return a page and names the offending parameter.

#### EF-02 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system returns no data and directs the user to authenticate.

### Acceptance criteria

- AC-01 — A new user's list shows the 19 default categories on two pages;
  `kind=income` shows the 6 income ones.
- AC-02 — `name=tra` returns "Transport" and "Travel".
- AC-03 — A category used by 3 expenses shows `expense_count` 3.
- AC-04 — Selecting "see entries" opens the expenses list, or the incomes
  list for an income category, with that category's filter applied.
- AC-05 — Default categories show no edit or delete action.


## UC-13 — Update a category

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-12, Glossary

### Objective

Allow an authenticated user to rename one of the categories they created or
change its description, through an idempotent partial update. Default
categories cannot be changed.

### Actors

- **User** (primary) — the owner of the category.
- **System** (secondary) — validates and persists the change.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the category shows the new values everywhere it
  appears, including in past expenses and closings.
- POST-02 — On failure, nothing changes.

### Data

| Parameter     | Type | Required | Constraints                              |
|---------------|------|----------|------------------------------------------|
| `id`          | text | Yes      | Identifies the category                  |
| `name`        | text | No       | 1–40 chars, trimmed, unique per user     |
| `description` | text | No       | 0–255 chars; empty clears it             |

### Functional requirements

- FR-01 — The update follows the same rules as UC-04 FR-01 to FR-07: only for
  the owner, partial, absolute values, idempotent, uniqueness excluding the
  category itself, and rejecting any other field.
- FR-02 — Renaming changes no expense and no balance; every expense in the
  category, past or present, shows the new name.
- FR-03 — Default categories (UC-11 FR-05) and the system "Internal
  transfer" category cannot be updated. The kind of a category cannot be
  changed.
- FR-04 — The update is started from the categories list (UC-12).

### Main flow

1. On the categories list, the user chooses to edit a category.
2. The system presents a form pre-filled with its current values.
3. The user changes them and confirms.
4. The system validates them.
   *(EF-01 if not found; EF-02 if the name is taken; EF-03 if a value is
   invalid; EF-05 if it is a default category.)*
5. The system persists the change and confirms: "Category updated".

### Exception flows

#### EF-01 — Category not found
Triggered at step 4 when no category of the user has that id.

1. The system changes nothing and displays "Category not found".

#### EF-02 — Name already taken
Triggered at step 4 when another of the user's categories has that name.

1. The system changes nothing and displays "Category name already exists".

#### EF-03 — Invalid value
Triggered at step 4 when the name or description breaks its constraints.

1. The system changes nothing and names the field.

#### EF-04 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

#### EF-05 — Default category
Triggered at step 4 when the category is a default one.

1. The system changes nothing and displays "Default categories cannot be
   changed".

### Acceptance criteria

- AC-01 — Renaming the user's category "Pets" to "Pet care" shows "Pet care"
  on every expense that used it, including consolidated months.
- AC-02 — Renaming it to "transport" fails with EF-02.
- AC-03 — Sending the same rename twice succeeds both times.
- AC-04 — Renaming the default category "Groceries" fails with EF-05.
- AC-05 — Sending `kind` fails with EF-03.


## UC-14 — Delete a category

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-12, Glossary

### Objective

Allow an authenticated user to delete one of the categories they created,
moving any expenses or incomes that use it to another category of the same
kind, since every entry must keep one. Default categories cannot be
deleted.

### Actors

- **User** (primary) — the owner of the category.
- **System** (secondary) — moves the expenses and deletes the category.

### Pre-conditions

- PRE-01 — The user is authenticated.

### Post-conditions

- POST-01 — On success, the category no longer exists, and every expense or
  income that used it belongs to the replacement category.
- POST-02 — On failure or cancellation, nothing changes.

### Data

| Parameter        | Type | Required         | Constraints                                   |
|------------------|------|------------------|-----------------------------------------------|
| `id`             | text | Yes              | Identifies the category                       |
| `replacement_id` | text | When it is used  | Another of the user's categories, same kind   |

### Functional requirements

- FR-01 — The system deletes a category only for the user who owns it.
- FR-02 — A category used by no expense is deleted physically after
  confirmation.
- FR-03 — A category used by expenses or incomes — in any state and any
  accounting month, removed ones included — can be deleted only after the
  user chooses a replacement category of the same kind, default or their
  own. Every such entry moves to the replacement, then the category is
  deleted physically.
- FR-04 — Before deleting, the system warns that the action cannot be undone
  and, when expenses are moved, how many and that consolidated months will
  show them under the replacement category. No balance changes.
- FR-05 — Default categories (UC-11 FR-05) cannot be deleted, so that
  expenses and incomes can always be registered.
- FR-06 — The system "Internal transfer" category cannot be deleted.
- FR-07 — After the deletion, its name may be used again.
- FR-08 — Deleting is started from the categories list (UC-12).

### Main flow

1. On the categories list, the user chooses to delete a category.
2. The system checks that the category is not a default one — default
   categories offer no delete action (UC-12 FR-03) — then shows the warning
   (FR-04) and, when the category is used, asks for a replacement category.
   *(EF-03 if it is a default category.)*
3. The user chooses the replacement, if needed, and confirms.
   *(If the user cancels, nothing changes.)*
4. The system validates the request.
   *(EF-01 if not found; EF-02 if the replacement is missing or invalid.)*
5. The system moves the expenses (FR-03), deletes the category, and
   confirms: "Category <name> deleted".

### Exception flows

#### EF-01 — Category not found
Triggered at step 4 when no category of the user has that id.

1. The system changes nothing and displays "Category not found".

#### EF-02 — Replacement missing or invalid
Triggered at step 4 when the category is used and no replacement is chosen,
or the replacement is the category itself, not one of the user's, or of the
other kind.

1. The system changes nothing and asks for a valid replacement; the flow
   returns to step 2.

#### EF-03 — Default category
Triggered at step 2, or at step 4 for a request made directly, when the
category is a default one.

1. The system changes nothing and displays "Default categories cannot be
   deleted".

#### EF-04 — Not authenticated
Triggered at step 1 when PRE-01 does not hold.

1. The system changes nothing and directs the user to authenticate.

### Acceptance criteria

- AC-01 — Deleting an unused category deletes it after confirmation.
- AC-02 — Deleting the user's category "Pets", used by 8 expenses, requires
  a replacement; choosing "Other" moves the 8 expenses to "Other" and deletes
  "Pets".
- AC-03 — No balance changes when expenses are moved.
- AC-04 — Deleting the default category "Leisure" fails with EF-03.
- AC-05 — Choosing an income category as replacement for an expense category
  fails with EF-02.

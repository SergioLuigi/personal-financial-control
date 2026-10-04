# 002 — List bank accounts

**Backlog:** T02 · **Use case:** UC-02 (all) · **Status:** Done

## Delivers

`GET /bank-accounts` returns the authenticated user's bank accounts, paged and
filtered.

## Request

Query parameters, all optional: `page` (default `0`), `name`, `description`,
`min_balance`, `max_balance`. Filters combine with AND.

## Decisions

- **Paging:** ten accounts per page, ordered by name only (it is unique per owner), so
  paging visits every account exactly once (AC-10). The response is the shared `PageResponse`
  (`items`, `page`, `total_items`, `total_pages`, minimum 1 page).
- **Scope:** every query is scoped to the owner the controller passes in; a user never sees
  another user's accounts (AC-05) and a user with none gets an empty page
  (AC-04).
- **Text filters** ignore case (AC-06); the **balance range** is inclusive
  (AC-07).
- **Balance filter:** it filters on the balance stored in `bank_account.balance`,
  through `BankAccountMinBalanceSpec` and `BankAccountMaxBalanceSpec`, so the
  page is cut by the database. The balance is to be informed by other means in a
  later phase.
- **Page:** read by Spring's `Pageable` (`@PageableDefault`, size 10); a
  negative or non-integer page is read as `0`, and the client may send `size`
  and `sort`. The default order is declared in the annotation
  (`sort = "name"`). The filters come in the record `ListBankAccountsRequest`.
- **Errors:** `400` for
  `max_balance` lower than `min_balance` (`errors[max_balance]`,
  `BankAccountMessage.INVALID_BALANCE_RANGE`).

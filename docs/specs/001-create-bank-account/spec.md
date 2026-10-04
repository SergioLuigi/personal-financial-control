# 001 — Create a bank account

**Backlog:** T01 · **Use case:** UC-01 (all) · **Status:** Done

## Delivers

`POST /bank-accounts` creates a bank account owned by the authenticated user.

## Request

```json
{ "name": " Savings ", "description": "Main account", "balance": 1000.00 }
```

`description` and `balance` are optional. Responds `201` with `id`, `name`,
`description` and `balance`; the name is returned as stored (`savings`).

## Decisions

- **Name:** trimmed and lowercased in the domain model (`NewBankAccount`), so
  every path to a name goes through the rule (`.claude/rules/naming.md`).
- **Uniqueness per owner, ignoring case and accents** (FR-03, AC-03, AC-09):
  `bank_account.name` uses the `utf8mb4_0900_ai_ci` collation, so the unique
  key `uk_bank_account_created_by_name_deleted_id` and the rule's lookup compare the same
  way. The rule gives the friendly error; the key is the safety net.
- **Balance:** `decimal(13,2)`, default `0.00`, may be negative; at most two
  decimal places. It belongs to no accounting month (FR-07).
- **Errors:** `409` with `errors[name]` = "Bank account name already exists";
  validation errors are `400` with the messages of EF-02 to EF-04.

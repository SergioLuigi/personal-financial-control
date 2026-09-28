# Glossary

Terms used with a single meaning across every use case document in
[use-cases/](use-cases/). When a use case and
this glossary disagree, the glossary wins and the use case must be fixed.

**Last updated:** 2026-09-28

## Records

- **Owner** — the user a record belongs to. Only the owner sees and changes
  it.
- **Audit fields** — who created a record and when (`created_by`,
  `created_at`), and who last changed it and when (`updated_by`,
  `updated_at`), kept for every record ([NFR-01](README.md#nfr-01--audit-trail)).
  They are filled in by the system, never by the user, and are not shown to
  the user. The author is the user's username, or `system` for the automated
  processes (UC-41 to UC-43). They are not the owner: the owner says whose
  record it is; the audit fields say who touched it.

## Money

- **Expense** — money spent by the user: a card purchase (UC-15) or a Pix
  (UC-16). Every expense has a funding source and a payment method.
- **Funding source** — where an expense's money comes from: a bank account's
  balance or a credit card's limit.
- **Payment method** — how an expense was paid: `card` or `pix`.
- **Category** — what an expense was for, such as groceries or transport, or
  what an income came from, such as a salary (UC-11). Every category is of
  one kind, `expense` or `income`. Every expense and every income has exactly
  one category of its kind; the category is mandatory. Every user starts with
  a default list of categories of each kind (UC-11 FR-05), which cannot be
  changed or deleted; the user may create, change and delete categories of
  their own. "Internal transfer" is a system category: it is never offered
  to the user.
- **Internal transfer** — a Pix from one of the user's bank accounts to
  another of their own bank accounts (UC-16 FR-14). The destination account
  is credited automatically. An internal transfer moves money between the
  user's accounts without spending it: it changes both balances, but is not
  counted in the total spent or received of any accounting month.
- **Charge** — one dated amount of an expense. A one-off expense has one
  charge; an expense in installments has one per installment; a recurring
  expense has one per month.
- **Income** — money received by the user into one of their bank accounts
  (UC-24). An income is one-off or recurring. Each amount received is a
  **receipt**: a one-off income has one; a recurring income, such as a
  salary, has one per month. A receipt raises the account's balance and,
  like a charge, belongs to an accounting month.
- **Balance adjustment** — a change made by hand to a bank account's balance
  (UC-01, UC-04), at any time. It is applied directly to the stored balance:
  it belongs to no accounting month, appears in no closing or month total,
  is not subject to the pending month lock, and is not listed in the
  account's details.

## Credit card bills

- **Bill** — for a credit card and an accounting month, the sum of the card's
  charges that belong to that month — installments, recurring charges and
  Pix on its limit — minus the card's refunds of that month, plus what was
  left unpaid from the previous bill.
- **Due date** — a bill is due on the card's due day (UC-06 FR-05) in the
  calendar month after the end of its accounting month's period.
- **Bill payment** — money taken from the card's own bank account (UC-06
  FR-03) to pay a bill, in full or in part, at any time — before the due date
  and before the month is consolidated included (UC-30). A bill is always
  paid from the bank account the card is associated with. It lowers the bank account's balance
  and releases the card's limit by the amount paid. It is not an expense:
  the purchases were already counted, so a payment is counted in no total
  spent or received.
- **Carried amount** — what is left unpaid of a bill on its due date moves to
  the next bill (UC-43), as it is: the system adds no fee or
  interest.
- **Bill status** — `open` while its accounting month is open, `closed` once
  it is consolidated, then `partially_paid`, `paid`, or `overdue` when its
  due date has passed and it is not fully paid.
- **Refund** — a credit on a credit card, such as a reversed purchase
  (UC-32). It lowers the bill of its accounting month and releases the
  card's limit, and it lowers the total spent in its category.
- **Available limit** — a card's balance: its limit minus what is owed on it.
  What is owed is the full value of its purchases (UC-15 FR-06), minus
  refunds and bill payments.

## Bank account balances

- **Balance** — a bank account's balance is its initial balance and balance
  adjustments plus its receipts (UC-24) and the internal
  transfers it received, minus the Pix paid from it (UC-16), the internal
  transfers it sent and the payments of its cards' bills (UC-30). Each
  amount counts from its date, following "Changes to the past". Money put
  into an account from outside is registered as an income.
- **Projected balance** — the balance minus what is still to be paid on the
  bills of the credit cards associated with the account: what the account
  will hold once its cards' bills are paid.

## Recording only

The system records money; it does not calculate it. Every amount the user
enters is final: the system adds no fee, interest or tax to it.

## Accounting months

- **Accounting month** — everything spent and received over a period of
  time, identified as `YYYY-MM`. Each user has a single sequence of
  accounting months, shared by all their bank accounts and credit cards;
  "September" means the same period everywhere in the system. An accounting
  month exists only once the user has created it (UC-33). There is at
  most one per calendar month, so at most 12 per year. The first accounting
  month is the first one created, normally when the first expense is
  registered. A month with no charge or receipt may be deleted
  (UC-34).
- **Closing day** — the day of the month on which each accounting month is
  expected to end, set by the user (UC-35); by default, the last day of the
  month. It defines each accounting month's period: accounting month
  `2026-09` with closing day 25 runs from 26 August to 25 September. On
  months shorter than the closing day, the period ends on the month's last
  day.
- **Current accounting month** — the accounting month whose period contains
  today.
- **Accounting month of a charge** — every charge and receipt belongs to
  exactly one accounting month, stored with it; it is
  not recalculated from the date. The user can always choose the accounting
  month, by default the current one, or the next open one if the current one
  is already consolidated. The chosen month must exist: when it does not,
  the user is required to create it first (UC-33). Installment *k* and
  the *k*-th recurring charge belong to the *k − 1*-th month after the first
  one; those later months do not need to exist yet — their charges appear in
  the forecast, and each month is created when the user needs it.
  Charges and receipts recorded by the automated processes (UC-41, UC-42) are
  always recorded in the accounting month stored with them — never moved to
  the current month or to the next open one — even when an earlier month is
  pending, the month is consolidated, or the month has not been created
  yet.
- **Open accounting month** — a created month not yet consolidated. A month
  stays open, even after its period has ended, until the user consolidates
  it; the system reminds the user of every open month whose period has
  ended.
- **Consolidation** — the user's statement that an accounting month is
  closed (UC-36), made whenever the user decides, before or after the closing
  day. Consolidation does not change the month's period. It takes a snapshot
  of the month: its **closing**.
- **Closing** — the snapshot taken at consolidation: what was spent and
  received in the month, and its bills, per funding
  source, and the balances it left. A closing can be
  corrected: later changes to charges of a consolidated month correct its
  closing (see "Changes to the past").
- **History and forecast** — the user sees each accounting month (UC-37):
  past and current months as history, and future months as a forecast built
  from the charges already registered for them — later installments,
  recurring charges and expenses assigned to future months.
- **Past expense** — an expense with at least one charge in a consolidated
  accounting month. A past expense can be changed (UC-19) or removed
  (UC-21), but only after the user is warned that the change affects the
  transaction history and the closing of those months.

## Warnings

A warning is shown before an action with financial consequences is carried
out, and the action proceeds only if the user acknowledges it. Warnings:

- show the actual values of the operation — amounts, balances, months,
  installments — never a generic sentence;
- are always shown: there is no option to stop showing them;
- are combined into one screen, with a single acknowledgement, when an
  action triggers more than one.

Placeholders in square brackets are filled in by the system; lines marked
*(if …)* appear only in that situation.

## Reminders

The system reminds the user of what needs attention — pending accounting
months, scheduled Pix due soon, recurring entries just made, bills due soon
or overdue — only inside the system, on the home summary (UC-38 FR-05). There are no e-mail or phone
notifications.

## Changes to the past

A change or removal never alters the current balance of any funding source
because of charges that belong to a **consolidated** accounting month. Such
charges are corrected in that month's closing. Charges in open accounting
months — the current one, a later one, or an earlier one not yet
consolidated — change the current balance normally.

Example: in September, with August consolidated, removing a one-off expense
of August corrects the August closing, and the bank account's or credit
card's current balance stays the same. Had August still been open, the
removal would have changed the current balance.

The rule applies equally to incomes. It has two exceptions:

- **Charges after an early consolidation** — a charge of a consolidated month
  whose date arrives after the consolidation (UC-36 FR-12) changes the
  current balance normally and is recorded as a correction in that month's
  closing.
- **New entries in a consolidated month** — registering a new expense,
  income, refund or bill payment in a consolidated month records money that
  really moved: it changes the current balance normally and is recorded as a
  correction in that month's closing. Only changes and removals of entries
  that already existed leave the current balance untouched.

The rule applies to credit cards as to bank accounts: a correction to a
consolidated month never changes the card's current available limit. When a
recurring expense is changed from a month on (UC-19 FR-15), the months still
open after the consolidated ones change the current balance normally.

## Pending month lock

An open accounting month whose period has ended is **pending**. While a month
is pending, nothing can be registered, changed, removed, stopped or resumed in
any **later** accounting month — expenses, incomes, refunds or bill payments
— until the user consolidates the pending month (UC-36).
The rule looks at the accounting month an operation is made in: the month
chosen for a new entry, or the month of the charges a change or removal
affects. The user is told as soon as they start the operation — before
filling in anything — that the pending month must be consolidated first, with
a way to consolidate it; the check is made again when they confirm, in case
they chose another month in the meantime. Operations in the pending month itself, and corrections to
consolidated months, remain possible. Automated processes (UC-41,
UC-42, UC-43) are never blocked.

Months can be created at any time, in any order (UC-33); creating a month
earlier than the others makes it pending as soon as its period has ended.

## Expense states

The states below apply to expenses and, except Scheduled, to incomes; for
an income, read "receipt" where the text says "charge".

- **Scheduled** — a Pix with a future date (UC-17). It is visible, marked
  as scheduled, and counts towards its funding source's balance only when its
  date arrives; from then on it is active.

- **Active** — the normal state of every expense. An active expense appears
  in every query and all its charges count towards its funding source's
  balance. A stopped expense returns to active when the user resumes it
  (UC-23).
- **Removed** — deleted by the user (UC-21). Removal is **logical**: the
  system keeps the record, but a removed expense never appears in any of the
  user's queries — details, lists, searches, balances — and behaves, for the
  user, exactly as if it did not exist. The user cannot undo a removal.
- **Removed month** — a single charge of a recurring expense (UC-21), or a
  single receipt of a recurring income (UC-28), removed by the user, in any accounting month. Removal is logical, as above: the
  charge no longer appears anywhere and no longer counts, while the expense
  and its other charges remain.
- **Stopped** — a recurring expense or income paused by the user (UC-22). A
  stopped expense or income **remains visible** in details, lists and searches, marked as
  stopped. It generates no charges and nothing new counts towards the
  balance while stopped; charges made before the stop remain and keep
  counting. Only recurring expenses and incomes can be stopped. Resuming (UC-23) makes it
  active again: it is charged from its first charge date after the
  resumption, and the months in which it was stopped remain without charges.

| State         | Visible to the user | New charges | Earlier charges count | Reversible          |
|---------------|---------------------|-------------|-----------------------|---------------------|
| Scheduled     | Yes, marked         | On its date | —                     | —                   |
| Active        | Yes                 | Yes         | Yes                   | —                   |
| Stopped       | Yes, marked         | No          | Yes                   | Yes, back to active |
| Removed       | No                  | No          | No                    | No                  |
| Removed month | No (that charge)    | —           | No (that charge)      | No                  |

## Deleting a bank account or credit card

When the user deletes a credit card (UC-10) or a bank account
(UC-05), what happens to each of its expenses, incomes and charges
depends on the accounting month it belongs to:

| Accounting month    | What happens                                                        |
|---------------------|---------------------------------------------------------------------|
| Current or future   | Deleted **physically**: erased from the system                      |
| Earlier, still open | Deleted physically, **only with the user's explicit authorization** |
| Consolidated        | Removed **logically**: kept, but never shown again (as "Removed")   |

Consolidated closings keep the amounts they recorded, so the history of
consolidated months does not change.

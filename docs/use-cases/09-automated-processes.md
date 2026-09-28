# Automated processes — Use cases

**Status:** Draft · **Last updated:** 2026-09-27

Use cases UC-41 to UC-43 of the personal financial control application.
Terms such as *accounting month*, *charge* or *removed* have the meaning given
in the [Glossary](../glossary.md), which wins over any use case text. References
to other use cases (UC-nn) point to the documents listed in the
[documentation index](../README.md). Flow diagrams are in
[Use case diagrams](../use-case-diagrams.md).

**Scope:** sign-up, sign-in and user account management are out of scope for
this document. Every use case assumes an authenticated user (PRE-01).

## Contents

- [UC-41 — Record recurring charges and receipts](#uc-41--record-recurring-charges-and-receipts)
- [UC-42 — Carry out scheduled Pix](#uc-42--carry-out-scheduled-pix)
- [UC-43 — Process bill due dates](#uc-43--process-bill-due-dates)


## UC-41 — Record recurring charges and receipts

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-15, UC-16, UC-22, UC-23, UC-24, UC-36, UC-38, Glossary

### Objective

Record, without any action from the user, each charge of an active recurring
expense and each receipt of an active recurring income when its date
arrives, so that balances, bills and accounting months reflect it.

### Actors

- **Scheduler** (primary) — the automated process that runs this use case.
- **System** (secondary) — records the charges and receipts.

### Pre-conditions

- PRE-01 — The scheduler is running.

### Post-conditions

- POST-01 — Every charge and receipt whose date has arrived is recorded
  exactly once and counts towards its source's balance.

### Execution

*(When and how often this process runs: to be defined.)*

### Functional requirements

- FR-01 — For every active recurring expense and income, the process records
  each charge or receipt whose date has arrived and that is not yet
  recorded, with the value in effect for its accounting month (UC-19
  FR-15, UC-26 FR-06).
- FR-02 — Nothing is recorded for a removed expense or income, a removed
  month, or a month in which the entry is stopped (Glossary).
- FR-03 — Each charge or receipt is recorded at most once, however many
  times the process runs.
- FR-04 — When the process has not run for some time, it records every charge
  and receipt whose date has passed since, each on its own date.
- FR-05 — A charge or receipt of a consolidated month recorded after the
  consolidation changes the current balance and is recorded as a correction
  in that month's closing (UC-36 FR-12).
- FR-06 — A recorded charge on a credit card becomes part of its accounting
  month's bill (UC-29).
- FR-07 — Each recorded entry produces a "recurring entry made" reminder on
  the home summary (UC-38 FR-05).
- FR-08 — The pending month lock does not apply to this process (Glossary).
- FR-09 — The amounts recorded are those registered by the user; the process
  adds nothing to them (Glossary, "Recording only").
- FR-10 — Each charge or receipt is recorded in its own accounting month —
  the one stored with it (Glossary, "Accounting month of a charge") — never
  in the current month or the next open one instead. This holds when an
  earlier month is pending, when its month is consolidated (FR-05), and when
  its month has not been created yet: the entry then belongs to that month
  and appears in it as soon as it is created.

### Main flow

1. The scheduler starts the process.
2. The system finds every active recurring expense and income with a charge
   or receipt whose date has arrived and that is not recorded yet.
3. For each, the system records it on its date and in its accounting month
   (FR-01 to FR-06).
4. The system creates the reminders (FR-07) and ends.

### Exception flows

#### EF-01 — Recording fails
Triggered at step 3 when a charge or receipt cannot be recorded.

1. The system records nothing for that entry and keeps it for the next run;
   the others are recorded normally.

### Acceptance criteria

- AC-01 — A recurring `39.90` subscription on the 15th is recorded on
  15 October and lowers the card's available limit by `39.90`.
- AC-02 — Running the process twice on the same day records it once.
- AC-03 — After three days without running, the process records the three
  days' charges, each on its own date.
- AC-04 — Nothing is recorded for a stopped subscription or a removed month.
- AC-05 — A recurring salary on the 5th is recorded on 5 October and raises
  the account's balance.
- AC-06 — With August pending on 15 September, the September charge of a
  recurring subscription is recorded in September, not in August or
  October.
- AC-07 — A recurring charge whose month `2026-11` has not been created is
  recorded on its date in `2026-11`, and appears there once the month is
  created.


## UC-42 — Carry out scheduled Pix

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-16, UC-17, UC-38, Glossary

### Objective

Make each scheduled Pix count, without any action from the user, when its
date arrives.

### Actors

- **Scheduler** (primary) — the automated process that runs this use case.
- **System** (secondary) — makes the Pix count.

### Pre-conditions

- PRE-01 — The scheduler is running.

### Post-conditions

- POST-01 — Every scheduled Pix whose date has arrived is active and counts
  towards its source's balance, exactly once.

### Execution

*(When and how often this process runs: to be defined.)*

### Functional requirements

- FR-01 — On its date, a scheduled Pix becomes active and counts as in UC-16
  FR-04 or FR-06. An internal transfer also credits its destination account
  (UC-16 FR-14).
- FR-02 — A scheduled Pix removed before its date is never carried out.
- FR-03 — Each scheduled Pix is carried out at most once, however many times
  the process runs; a missed date is caught up on the next run, on its own
  date.
- FR-04 — A recurring scheduled Pix, once active, is charged every month by
  UC-41.
- FR-05 — The pending month lock does not apply to this process (Glossary).
- FR-06 — The Pix counts in its own accounting month, the one chosen when it
  was scheduled (UC-17 FR-05), never in another month instead — even when an
  earlier month is pending, its month is consolidated, or its month has not
  been created yet (Glossary, "Accounting month of a charge").

### Main flow

1. The scheduler starts the process.
2. The system finds every scheduled Pix whose date has arrived.
3. For each, the system makes it active and counts it on its date (FR-01).
4. The process ends.

### Exception flows

#### EF-01 — Carrying out fails
Triggered at step 3 when a Pix cannot be carried out.

1. The Pix stays scheduled and is retried on the next run; the others are
   carried out normally.

### Acceptance criteria

- AC-01 — A `200.00` Pix scheduled for 3 October is active from 3 October,
  and the account's balance drops by `200.00` that day.
- AC-02 — A scheduled internal transfer credits the destination account on
  its date.
- AC-03 — Running the process twice counts the Pix once.
- AC-04 — A Pix scheduled in `2026-10` is carried out in `2026-10` even while
  September is pending.


## UC-43 — Process bill due dates

**Status:** Draft · **Last updated:** 2026-09-27 · **Related:** UC-08, UC-29, UC-30, UC-38, Glossary

### Objective

When a credit card bill reaches its due date, settle its status and carry
what is still unpaid to the next bill, without any action from the user.

### Actors

- **Scheduler** (primary) — the automated process that runs this use case.
- **System** (secondary) — updates the bills.

### Pre-conditions

- PRE-01 — The scheduler is running.

### Post-conditions

- POST-01 — Every bill whose due date has passed is either `paid` or
  `overdue`, and what was unpaid on it is part of the next bill.

### Execution

*(When and how often this process runs: to be defined.)*

### Functional requirements

- FR-01 — When a bill's due date has passed and it is fully paid, its status
  is `paid`.
- FR-02 — When a bill's due date has passed and something is still unpaid,
  its status becomes `overdue`, and the unpaid amount is added, as it is, to
  the next bill of the same card as its carried amount (Glossary, "Carried
  amount"). The system adds no fee or interest (Glossary, "Recording only").
- FR-03 — A payment made after the amount was carried (UC-30) is applied to
  the next bill, which now includes it.
- FR-04 — Each bill is processed at most once; a missed due date is caught up
  on the next run.
- FR-05 — The next bill's accounting month does not need to exist: the carried
  amount belongs to it and appears in its forecast until the month is
  created.
- FR-06 — Due dates changed by the user (UC-08 FR-13) are processed on the
  next run, including a due date already in the past.
- FR-07 — The pending month lock does not apply to this process (Glossary).

### Main flow

1. The scheduler starts the process.
2. The system finds every bill whose due date has passed and that has not
   been processed yet.
3. For each, the system sets its status and carries what is unpaid
   (FR-01, FR-02).
4. The process ends.

### Exception flows

#### EF-01 — Processing fails
Triggered at step 3 when a bill cannot be processed.

1. The bill is retried on the next run; the others are processed normally.

### Acceptance criteria

- AC-01 — A September bill of `350.00` with `200.00` paid becomes `overdue`
  after 10 October, and the October bill includes `150.00` carried, with
  nothing added.
- AC-02 — A fully paid bill becomes `paid` on its due date.
- AC-03 — Running the process twice carries the amount once.
- AC-04 — After the user moves the due day to a date already past (UC-08
  FR-13), the next run makes the unpaid bill `overdue` and carries its
  amount.

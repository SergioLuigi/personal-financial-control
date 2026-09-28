# Naming

Every name the project creates is in lowercase: the names users give to their
records, and the names of database objects (see `database.md`).

## Names given by users

- Every `name` a user gives to a record — a bank account, a credit card, a
  category, and any record added later — is stored in lowercase, after
  trimming surrounding whitespace. Accents are kept: "Poupança" is stored as
  "poupança".
- Lowercasing is a domain rule: the domain model applies it with
  `toLowerCase(Locale.ROOT)`, so every path that creates or changes a name
  goes through it. Requests may trim before validating, but never rely on
  the client to lowercase.
- Names are returned as stored, in lowercase.
- Default data created by the system, such as the default categories, follows
  the same rule.

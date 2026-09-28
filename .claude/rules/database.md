# Database

## Object names

- Tables, columns, constraints, indexes and Liquibase changeset ids are
  written in lowercase `snake_case`, such as `bank_account`, `created_by`,
  `uk_bank_account_created_by_name`.
- Constraints and indexes carry a prefix for their kind — `pk_`, `uk_`,
  `fk_`, `idx_` — followed by the table name and, for `uk_`, `fk_` and
  `idx_`, the columns involved.
- JPA mappings never quote or upper-case a name. MySQL on Linux compares table
  names case-sensitively, so a name must be spelled the same way everywhere.

## UUIDs

- Every UUID column is `varchar(36)`, holding the canonical text form
  (`0b5f7a3e-6f0d-4c1e-9a53-2f4a8b1c9d10`) — ids, foreign keys and the tables
  of libraries such as Spring Modulith alike. Never `binary(16)` or `char(36)`.
- Hibernate maps every `java.util.UUID` to `VARCHAR` through
  `hibernate.type.preferred_uuid_jdbc_type` in `application.yaml`, so entities
  declare UUID fields with no type annotation.

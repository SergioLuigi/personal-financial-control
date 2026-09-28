# Architecture

The project follows Clean Architecture inside a Spring Modulith modular
monolith. The `bankaccount` module is the reference implementation: new modules
mirror its structure and naming.

## Modules

- Each top-level package under `br.com.sergioluigi.personal_financial_control`
  is an application module, declared in its `package-info.java` with
  `@ApplicationModule` and `@NullMarked`.
- `commons` holds cross-cutting code (`audit`, `exception`, `security`), each
  package exposed as a `@NamedInterface`. Business modules may depend on
  `commons::*` and on nothing else unless explicitly allowed.
- `ModularityTests` must keep passing.

## Layers

Each module is split into three layers. Dependencies point inward only:
`infra` → `application` → `domain`.

```
<module>/
├── domain/
│   ├── message/                # business error messages
│   ├── model/                  # domain models
│   ├── repository/             # repository interfaces (ports)
│   │   └── model/              # query inputs, such as filters
│   └── rule/                   # business rules
├── application/
│   └── usecase/                # one interface per use case
│       └── impl/               # its implementation
└── infra/
    ├── rest/                   # controllers
    │   └── dto/                # requests and responses
    └── repository/             # repository implementations (adapters)
        ├── entity/             # JPA entities
        └── spec/               # JPA Specifications
```

### Domain

- Models are immutable Java `record`s with no JPA or web annotations.
- Repository interfaces are defined here and speak only in domain models.
  Spring Data's `Page` and `Pageable` are the only persistence types allowed.
- Each business rule is its own class named after the invariant it enforces
  (`<Subject><Condition>Rule`, e.g. `BankAccountNameIsUniqueForOwnerRule`),
  exposing a `check(...)` method that throws a `BusinessException` when
  violated.
- Each module's business error messages live in `domain/message/`, in an enum
  named `<Subject>Message` that implements `commons.exception.BusinessMessage`:
  one constant per situation, with its HTTP status, the request field it
  refers to (or `null`) and its English text. There is no shared message
  bundle.
- The domain never depends on `application` or `infra`.

### Application

- One interface per use case, named `<Verb><Subject>UseCase`, with a single
  `execute(...)` method that takes and returns domain types.
- The implementation lives in `impl/`, is package-private, is named
  `<UseCaseName>Impl`, and is annotated with `@Service`. Writes are
  `@Transactional`.
- Use cases orchestrate: they call rules and repository interfaces. They never
  touch JPA entities, DTOs or HTTP concerns.

### Infra

- Controllers are package-private, depend only on use case interfaces, and map
  between DTOs and domain models. They hold no business logic.
- Handler methods return the response DTO itself (or `void` when there is no
  body), never `ResponseEntity`. The status code is declared with
  `@ResponseStatus`.
- Request DTOs are records carrying Bean Validation annotations and a
  `toDomain()` method. Response DTOs are records with a static `from(domain)`
  factory. Domain models never leave through the API.
- JPA entities are separate from domain models, with `from(domain)` and
  `toDomain()` conversions. Audit fields come from the embedded
  `commons.audit.AuditEntity`. Its `createdBy` is the record's owner: the
  entity sets it from the domain model's owner, since auditing fills in only
  `createdAt`, `updatedBy` and `updatedAt`.
- The repository adapter (`<Subject>RepositoryImpl`) is package-private,
  implements the domain interface and delegates to a package-private Spring
  Data interface (`<Subject>JpaRepository`).
- Every query is scoped to the current user through `commons.security.CurrentUser`.
- Dynamic filters are composed from one `Specification` record per criterion,
  each returning `null` when its input is empty.

## Errors

Every business rule violation — a duplicate, a missing record, a forbidden
operation — is raised as the single `commons.exception.BusinessException`,
built from a `BusinessMessage` constant of the module
(`new BusinessException(BankAccountMessage.NAME_ALREADY_EXISTS)`). No module
creates its own exception classes. The message sets the HTTP status and, when
it has a field, adds an entry to the `errors` list of the `ProblemDetail`.
`GlobalExceptionHandler` turns it, and Bean Validation and unreadable-body
errors, into responses.

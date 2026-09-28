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
  exposing a `check(...)` method that throws an exception from
  `commons.exception` when violated.
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
- Request DTOs are records carrying Bean Validation annotations and a
  `toDomain()` method. Response DTOs are records with a static `from(domain)`
  factory. Domain models never leave through the API.
- JPA entities are separate from domain models, with `from(domain)` and
  `toDomain()` conversions. Audit fields come from the embedded
  `commons.audit.AuditEntity`.
- The repository adapter (`<Subject>RepositoryImpl`) is package-private,
  implements the domain interface and delegates to a package-private Spring
  Data interface (`<Subject>JpaRepository`).
- Every query is scoped to the current user through `commons.security.CurrentUser`.
- Dynamic filters are composed from one `Specification` record per criterion,
  each returning `null` when its input is empty.

## Errors

Errors are raised as subclasses of `commons.exception.ApplicationException`
(`NotFoundException`, `AlreadyExistsException`, …), which carry the HTTP status
and a `ProblemDetail`. `GlobalExceptionHandler` turns them into responses.

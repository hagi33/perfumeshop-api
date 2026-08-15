# PerfumeShop API — Backend

E-commerce REST API for a perfumery shop. Modular monolith with Spring Boot 4 and Java 21.

## Stack
- Java 21, Spring Boot 4.0.7 (Spring Security 7)
- PostgreSQL + Flyway (Flyway owns the schema; Hibernate only validates)
- Spring Data JPA, MapStruct, Lombok
- JWT (jjwt 0.12.6)
- springdoc-openapi (Swagger at /swagger-ui.html)
- Maven. Dev base URL: http://localhost:8080

## Architecture — modular monolith (ALWAYS respect)
Three business modules (`catalog`, `user`, `order`) plus `shared` (cross-cutting).
Each module separates `api/` (public boundary) from `internal/` (implementation).

Non-negotiable modularity rules:
- `internal/` classes are **package-private** whenever possible. Only what's
  declared in `api/` is visible from other modules. Do not raise something to
  `public` unless sibling-subpackage visibility within the same module requires
  it; if you do, add a comment explaining why.
- Modules communicate ONLY through `api/` interfaces (CatalogApi, UserApi) and
  their public DTOs (CatalogItem). Never import an `internal` class from another
  module.
- **No JPA relationship crosses module boundaries.** Cross-module references are
  stored as plain identifiers (Long), not as @ManyToOne. E.g. cart_items.perfume_id
  has NO FK to perfumes; integrity is enforced in code by validating against
  CatalogApi. Within a single module, JPA relationships ARE fine.
- Large modules are subdivided into subpackages by area (e.g. `order/internal`
  → `cart/` and `order/`), each with its own `dto/` and `exception/` as needed.

## Layers within each module
- **Controller**: translates HTTP and delegates. No business logic.
- **Service**: business logic and transactions (@Transactional where needed).
- **Repository**: data access (Spring Data JPA).
- **Mapper** (MapStruct): converts between entity and DTO.
  Business logic never lives in the controller. Logic that protects an invariant
  lives in the entity, next to the data it protects (e.g. Perfume.decreaseStock).

## Data conventions
- Immutable DTOs as `record`. NEVER expose the JPA entity in a response.
- `BigDecimal` for money (precision 6, scale 2).
- Entities use Lombok (@Getter/@Setter/@Builder/@NoArgsConstructor/@AllArgsConstructor),
  package-private.
- Flyway owns the schema (`ddl-auto: validate`). ANY schema change is a new
  migration `V{n}__description.sql` in src/main/resources/db/migration. NEVER
  edit an already-applied migration (it breaks the checksum); always a new version.
- Enums persisted as STRING (@Enumerated(EnumType.STRING)).

## Errors
- ProblemDetail (RFC 7807). Each module has its own ExceptionHandler in its
  `exception/` subpackage; `shared/exception/GlobalExceptionHandler` covers
  validation and unhandled cases.
- Every domain exception must map to its correct HTTP status (not found → 404,
  state/stock conflict → 409, validation → 400). Do NOT throw a bare
  RuntimeException: it ends up as 500 and hides the real cause.

## Security
- Stateless JWT. Catalog GET is public; catalog writes are ADMIN-only; cart and
  orders require an authenticated user.
- The user id comes from the token, NEVER from the URL or body.

## Tests
- Current state: the suite is essentially empty (only contextLoads); tests are
  acknowledged pending debt. Therefore: when you build new logic (services,
  business rules), WRITE unit tests for it. This is an active project goal, not
  an extra.
- Run: `mvn test`. Compile without tests: `mvn compile`.
- Start the app: `mvn spring-boot:run` (requires PostgreSQL on localhost:5432).

## How to work in this repo
- Explain every architectural decision BEFORE applying it.
- For a schema change, the Flyway migration comes first and defines the contract.
- Package refactors: move few classes, COMPILE (`mvn compile`), repeat. Don't
  move ten and compile at the end: orphaned imports pile up.
- Do not introduce new dependencies without proposing and justifying them first.
- Do not use Spring Modulith: modularity is plain package-private (decided).
# PerfumeShop API

**English** · [Español](README.es.md)

E-commerce REST API for a fragrance shop, built as a **modular monolith** with Spring Boot 4 and Java 21. Backend for the PerfumeShop project ([Angular frontend here](https://github.com/hagi33/perfumeshop-web)).

## Tech stack

- **Java 21** · **Spring Boot 4** (Spring Security 7)
- **PostgreSQL** with **Flyway** migrations
- **Spring Data JPA**, **MapStruct**, **Lombok**
- **JWT** authentication (jjwt)
- **Thymeleaf** for transactional email templates
- **Anthropic Java SDK** for the AI assistant
- **springdoc-openapi** (Swagger UI)
- Maven

## Architecture

A single deployable application, split internally into business modules — `catalog`, `user`, `order`, `chat` — plus a cross-cutting `shared` package. Each module exposes a narrow public boundary (`api/`) and hides its implementation (`internal/`).

The design follows a few deliberate rules:

- **Enforced module boundaries.** Classes under `internal/` are package-private wherever possible, so the compiler itself prevents one module from reaching into another's internals. Only what a module publishes in `api/` is visible from outside.
- **Modules talk through interfaces, never entities.** `order` and `chat` depend on `CatalogApi`, not on the `Perfume` entity. No JPA relationship crosses a module boundary — cross-module references are stored as plain `Long` ids, and integrity is enforced in code (validating against `CatalogApi`) rather than by foreign keys.
- **A domain event as an extension point.** `catalog` publishes a `StockDecreasedEvent`, and `order` publishes an `OrderPaidEvent` that triggers the confirmation email — decoupling side effects from the core transaction.
- **Subpackages by area.** Modules that grew (like `order`, split into `cart/` and `order/`) are subdivided so no package becomes a dumping ground.

### Module structure

```
com.fabio.perfumeshop_api
├── catalog/     Perfume catalog, olfactory notes (fragrance pyramid)
│   ├── api/         CatalogApi, CatalogItem, CatalogRecommendationItem,
│   │                StockDecreasedEvent, InsufficientStockException
│   └── internal/    Perfume, Note, PerfumeNote, services, controller, mapper
├── user/        Registration, JWT login, roles (USER / ADMIN)
│   ├── api/         UserApi
│   └── internal/    auth/ (JWT, filter, security) · user/ (domain) · config/
├── order/       Cart, transactional checkout, simulated payment, history
│   └── internal/    cart/ · order/ · exception/
├── chat/        AI shopping assistant (Anthropic SDK)
│   └── internal/    ChatController, ChatService, dto/
└── shared/      GlobalExceptionHandler (cross-cutting)
```

## Layered design

Within each module: **controllers** translate HTTP and delegate, **services** hold business logic and transactions, **repositories** handle persistence, and **mappers** (MapStruct) convert between entities and immutable record DTOs. Business logic never lives in a controller; invariants live in the entity next to the data they protect.

## Features

- **Catalog** — full CRUD (writes restricted to `ADMIN`), with a Fragrantica-style **fragrance pyramid**: each perfume relates to reusable notes through a `PerfumeNote` association entity carrying the pyramid level (top / heart / base).
- **Authentication** — stateless JWT, BCrypt-hashed passwords, `USER` / `ADMIN` roles. The user id is always taken from the token, never from the request.
- **Cart & orders** — cart management, transactional checkout (decrements stock and freezes prices), a simulated pay step (`PENDING` → `PAID`), and order history.
- **Order confirmation email** — on payment, an `OrderPaidEvent` is published and a `@TransactionalEventListener(AFTER_COMMIT)` sends an HTML email (Thymeleaf template) — only if the payment transaction actually committed, so a mail failure never breaks a paid order.
- **AI assistant** — a `/api/chat` endpoint backed by the Anthropic Java SDK. The current catalog (names, brands, families, notes) is injected into the system prompt, so Claude recommends real perfumes from the shop. Conversation memory is handled by replaying the message history on each call.

## Key endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/register` · `/login` | Register / log in (returns JWT) |
| `GET`  | `/api/perfumes` · `/{id}` | Browse catalog (public) |
| `POST`/`PUT`/`DELETE` | `/api/perfumes` | Manage catalog (ADMIN) |
| `GET`/`POST`/`PUT`/`DELETE` | `/api/cart` · `/items` | Manage cart (auth) |
| `POST` | `/api/orders/checkout` · `/{id}/pay` | Checkout and pay (auth) |
| `GET`  | `/api/orders` | Order history (auth) |
| `POST` | `/api/chat` | AI assistant (auth) |

## Running locally

Requires **Java 21**, **Maven**, and **PostgreSQL** running on `localhost:5432` with a database named `perfumeshop-api`.

Configuration and secrets are read from environment variables (with development defaults where safe):

| Variable | Purpose |
|----------|---------|
| `DB_USER`, `DB_PASSWORD`, `DB_NAME` | Database credentials |
| `JWT_SECRET` | JWT signing key |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP (e.g. Mailtrap in dev) |
| `ANTHROPIC_API_KEY` | Anthropic API key for the chat |

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `/swagger-ui.html`

Flyway creates and seeds the schema on startup. To make an admin, promote a user in the database (`UPDATE users SET role = 'ADMIN' WHERE email = '…'`) and log in again.

## Design decisions

- Immutable `record` DTOs; the JPA entity is never exposed.
- `BigDecimal` for money; Flyway owns the schema (Hibernate only validates).
- Orders freeze name and price at purchase time (immutable historical record).
- Errors as `ProblemDetail` (RFC 7807); each module handles its own exceptions.
- Secrets never hard-coded — all sensitive config comes from environment variables.

## Status

Catalog, users, cart, orders, notes, transactional email and the AI assistant are implemented. Test coverage is a work in progress. Deployment is the next step.
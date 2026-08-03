# perfumeshop-api


#TODO: Tests unitarios y de integración.
# PerfumeShop API

API REST de comercio electrónico para una tienda de fragancias, construida como monolito modular con Spring Boot 4 y Java 21.

## Stack tecnológico

- **Java 21** + **Spring Boot 4.0.7** (Spring Security 7)
- **PostgreSQL 16** (Docker) con **Flyway** para migraciones
- **Spring Data JPA**, **MapStruct**, **Lombok**
- **JWT** (jjwt 0.12.6) para autenticación
- **springdoc-openapi** (Swagger UI)
- **Maven**

## Arquitectura

Monolito modular organizado en tres módulos (`catalog`, `user`, `order`), cada uno con un paquete `api/` público y un paquete `internal/` privado. Las clases internas son package-private, de modo que los módulos solo se comunican a través de sus interfaces públicas. Las referencias entre módulos se guardan como identificadores simples, sin relaciones JPA ni claves foráneas que crucen las fronteras.

## Módulos

- **catalog** — Catálogo de perfumes con CRUD (escritura solo para ADMIN). Expone `CatalogApi` (`findById`, `decreaseStock`).
- **user** — Registro, login con JWT y roles (`USER` / `ADMIN`). Contraseñas cifradas con BCrypt. Expone `UserApi` (`findIdByEmail`).
- **order** — Carrito de compra, checkout transaccional (descuenta stock y congela precios), pago simulado e historial de pedidos.

## Seguridad

Autenticación sin estado con JWT. La consulta del catálogo es pública; la escritura requiere rol ADMIN; el carrito y los pedidos requieren usuario autenticado. El id del usuario se obtiene del token, no de la URL, garantizando que nadie accede a recursos de otro.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Login (devuelve JWT) |
| GET | `/api/perfumes` · `/{id}` | Consultar catálogo (público) |
| POST · PUT · DELETE | `/api/perfumes` | Gestionar catálogo (ADMIN) |
| GET · POST · PUT · DELETE | `/api/cart` · `/items` | Gestionar carrito |
| POST | `/api/orders/checkout` | Confirmar compra |
| POST | `/api/orders/{id}/pay` | Pagar pedido |
| GET | `/api/orders` | Historial de pedidos |

## Puesta en marcha

```bash
docker compose up -d      # PostgreSQL
mvn spring-boot:run       # arranca la app (Flyway crea el esquema)
```

API en `http://localhost:8080` · Swagger en `/swagger-ui.html`.

Las credenciales y el secreto JWT se leen de variables de entorno (`DB_USER`, `DB_PASSWORD`, `JWT_SECRET`) con valores por defecto para desarrollo. Para crear un administrador se promociona un usuario en la base de datos y se vuelve a iniciar sesión:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'tu@email.com';
```

## Decisiones de diseño

- DTOs inmutables (`record`); nunca se expone la entidad JPA.
- `BigDecimal` para el dinero.
- Flyway es dueño del esquema; Hibernate solo valida.
- Los pedidos congelan nombre y precio en el momento de la compra (registro histórico inmutable).
- Errores con `ProblemDetail` (RFC 7807).

## Estado

Implementado: catálogo, usuarios y pedidos completos. Pendiente: tests, frontend y pulido menor.

# PerfumeShop API

API REST de comercio electrónico para una tienda de fragancias, construida como **monolito modular** con Spring Boot 4 y Java 21.

## Stack tecnológico

- **Java 21** + **Spring Boot 4.0.7** (Spring Security 7)
- **PostgreSQL** con **Flyway** para migraciones
- **Spring Data JPA**, **MapStruct**, **Lombok**
- **JWT** (jjwt 0.12.6) para autenticación
- **springdoc-openapi** (Swagger UI)
- **Maven**

## Arquitectura

Monolito modular: una sola aplicación desplegable, dividida internamente en tres módulos de negocio (`catalog`, `user`, `order`) más un paquete de soporte transversal (`shared`). Cada módulo separa su **API pública** (`api/`) de su **implementación** (`internal/`).

Dentro de `internal/`, los módulos que han crecido se subdividen en subpaquetes por área funcional (por ejemplo `order` separa `cart/` y `order/`, y `user` separa `auth/`, `user/` y `config/`), cada uno con su propio `dto/` y `exception/` cuando lo necesita. Así ningún paquete se convierte en un cajón de sastre y cada archivo vive junto a los que comparten su responsabilidad.

### Estructura de paquetes

```
com.fabio.perfumeshop_api
│
├── catalog/
│   ├── api/                            Frontera pública del módulo
│   │   ├── CatalogApi                  Interfaz (findById, decreaseStock)
│   │   ├── CatalogItem                 DTO expuesto a otros módulos
│   │   ├── InsufficientStockException
│   │   └── StockDecreasedEvent         Evento de dominio (stock disminuido)
│   └── internal/
│       ├── Perfume                     Entidad JPA
│       ├── PerfumeRepository
│       ├── PerfumeService              Implementa CatalogApi
│       ├── PerfumeController           /api/perfumes
│       ├── PerfumeMapper
│       ├── CreatePerfumeRequest / PerfumeResponse
│       ├── Concentration / Gender / OlfactoryFamily   Enums del dominio
│       └── exception/
│           ├── CatalogExceptionHandler
│           └── ResourceNotFoundException
│
├── user/
│   ├── api/
│   │   └── UserApi                     Interfaz pública (findIdByEmail)
│   └── internal/
│       ├── auth/                       Autenticación y seguridad
│       │   ├── AuthController          /api/auth
│       │   ├── AuthService             Registro y login
│       │   ├── JwtService              Genera y valida tokens
│       │   ├── JwtAuthFilter           Intercepta cada petición
│       │   ├── AppUserDetailsService
│       │   └── dto/
│       │       ├── AuthResponse
│       │       ├── LoginRequest
│       │       └── RegisterRequest
│       ├── user/                       Dominio usuario
│       │   ├── User                    Entidad JPA
│       │   ├── Role                    Enum (USER, ADMIN)
│       │   ├── UserRepository
│       │   └── UserService             Implementa UserApi
│       ├── config/                     Configuración
│       │   ├── SecurityConfig          Cadena de filtros y reglas de acceso
│       │   ├── PasswordConfig          Bean BCrypt
│       │   └── OpenApiConfig           Configuración de Swagger
│       └── exception/
│           ├── UserExceptionHandler
│           ├── EmailAlreadyExistsException
│           └── InvalidCredentialsException
│
├── order/
│   └── internal/
│       ├── cart/                       Carrito de compra
│       │   ├── Cart / CartItem         Entidades
│       │   ├── CartRepository
│       │   ├── CartService             Lógica del carrito
│       │   ├── CartController          /api/cart
│       │   ├── CartMapper
│       │   └── dto/
│       │       ├── AddItemRequest / UpdateItemRequest
│       │       └── CartResponse / CartItemResponse
│       ├── order/                      Pedidos
│       │   ├── Order / OrderItem       Entidades
│       │   ├── OrderStatus             Enum (PENDING, PAID, CANCELLED)
│       │   ├── OrderRepository
│       │   ├── OrderService            Checkout, pago, historial
│       │   ├── OrderController         /api/orders
│       │   ├── OrderMapper
│       │   └── dto/
│       │       └── OrderResponse / OrderItemResponse
│       └── exception/
│           ├── OrderExceptionHandler
│           ├── EmptyCartException
│           ├── InvalidOrderStateException
│           └── ResourceNotFoundException
│
└── shared/
    └── exception/
        └── GlobalExceptionHandler      Validación + catch-all
```

Las clases de cada `internal/` son **package-private** siempre que es posible: el compilador de Java impide que un módulo acceda a las tripas de otro. Solo lo declarado en `api/` es visible desde fuera del módulo. (Algunas clases dentro de un módulo son `public` por necesidad de visibilidad entre subpaquetes hermanos —por ejemplo entre `cart/` y `order/`—; en esos casos el acoplamiento es interno al módulo e intencional, no una API pública.)

## Comunicación entre módulos

Los módulos se comunican exclusivamente a través de las interfaces públicas del paquete `api/`. El módulo `order` depende de `CatalogApi` y `UserApi`, pero nunca de sus implementaciones concretas ni de sus entidades.

Como punto de extensión, `catalog` publica un **evento de dominio** (`StockDecreasedEvent`) cada vez que se descuenta stock. Hoy no tiene consumidores, pero deja abierta la puerta a reaccionar a ese hecho (avisos de agotado, registro de movimientos de inventario) sin tocar el catálogo.

Además, ninguna relación JPA cruza las fronteras entre módulos: las referencias se guardan como identificadores simples (`Long`), no como relaciones `@ManyToOne`. Por eso `cart_items.perfume_id` no tiene clave foránea hacia `perfumes` — la integridad la garantiza el código validando contra `CatalogApi`, no la base de datos. Esto mantiene los módulos desacoplados también a nivel de persistencia.

## Capas dentro de cada módulo

Cada módulo sigue el mismo patrón por capas: el **controller** traduce HTTP y delega; el **service** contiene la lógica de negocio y las transacciones; el **repository** habla con la base de datos; los **mappers** convierten entre entidades y DTOs. La lógica nunca vive en el controller.

## Módulos

- **catalog** — Catálogo de perfumes con CRUD (escritura solo para `ADMIN`). Expone `CatalogApi` (`findById`, `decreaseStock`) y el evento `StockDecreasedEvent`.
- **user** — Registro, login con JWT y roles (`USER` / `ADMIN`). Contraseñas cifradas con BCrypt. Expone `UserApi` (`findIdByEmail`).
- **order** — Carrito de compra, checkout transaccional (descuenta stock y congela precios), pago simulado e historial de pedidos.

## Seguridad

Autenticación sin estado con JWT. La consulta del catálogo es pública; la escritura requiere rol `ADMIN`; el carrito y los pedidos requieren usuario autenticado. El id del usuario se obtiene del token, no de la URL, garantizando que nadie accede a recursos de otro.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Registrar usuario |
| `POST` | `/api/auth/login` | Login (devuelve JWT) |
| `GET` | `/api/perfumes` · `/{id}` | Consultar catálogo (público) |
| `POST` · `PUT` · `DELETE` | `/api/perfumes` | Gestionar catálogo (ADMIN) |
| `GET` · `POST` · `PUT` · `DELETE` | `/api/cart` · `/items` | Gestionar carrito |
| `POST` | `/api/orders/checkout` | Confirmar compra |
| `POST` | `/api/orders/{id}/pay` | Pagar pedido |
| `GET` | `/api/orders` | Historial de pedidos |

## Puesta en marcha

Requisitos: **Java 21**, **Maven** y una instancia de **PostgreSQL** accesible en `localhost:5432` con una base de datos llamada `perfumeshop-api`.

```bash
mvn spring-boot:run       # arranca la app (Flyway crea el esquema)
```

- API en `http://localhost:8080`
- Swagger UI en `/swagger-ui.html`

Las credenciales y el secreto JWT se leen de variables de entorno (`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`) con valores por defecto para desarrollo. Para crear un administrador se promociona un usuario en la base de datos y se vuelve a iniciar sesión:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'tu@email.com';
```

## Decisiones de diseño

- DTOs inmutables (`record`); nunca se expone la entidad JPA.
- `BigDecimal` para el dinero.
- Flyway es dueño del esquema; Hibernate solo valida.
- Los pedidos **congelan** nombre y precio en el momento de la compra (registro histórico inmutable).
- Errores con `ProblemDetail` (RFC 7807); cada módulo maneja sus propias excepciones, con un `GlobalExceptionHandler` transversal para validación y casos no controlados.
- Comunicación entre módulos por interfaces `api/` y un evento de dominio; sin relaciones JPA cruzando fronteras.

## Estado

**Implementado:** catálogo, usuarios y pedidos completos; frontend Angular en desarrollo aparte. **Pendiente:** tests, despliegue y pulido menor.

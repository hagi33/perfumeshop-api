# PerfumeShop API


 **Español** · [English](README.md) 

API REST de comercio electrónico para una tienda de fragancias, construida como **monolito modular** con Spring Boot 4 y Java 21. Backend del proyecto PerfumeShop ([frontend en Angular aquí](https://github.com/hagi33/perfumeshop-web)).

## Stack tecnológico

- **Java 21** · **Spring Boot 4** (Spring Security 7)
- **PostgreSQL** con migraciones **Flyway**
- **Spring Data JPA**, **MapStruct**, **Lombok**
- Autenticación **JWT** (jjwt)
- **Thymeleaf** para las plantillas de correo transaccional
- **SDK de Java de Anthropic** para el asistente de IA
- **springdoc-openapi** (Swagger UI)
- Maven

## Arquitectura

Una sola aplicación desplegable, dividida internamente en módulos de negocio — `catalog`, `user`, `order`, `chat` — más un paquete transversal `shared`. Cada módulo expone una frontera pública estrecha (`api/`) y esconde su implementación (`internal/`).

El diseño sigue unas reglas deliberadas:

- **Fronteras de módulo impuestas por el compilador.** Las clases de `internal/` son package-private siempre que es posible, de modo que el propio compilador impide que un módulo acceda a las tripas de otro. Solo lo que un módulo publica en `api/` es visible desde fuera.
- **Los módulos se comunican por interfaces, nunca por entidades.** `order` y `chat` dependen de `CatalogApi`, no de la entidad `Perfume`. Ninguna relación JPA cruza una frontera de módulo: las referencias entre módulos se guardan como simples `Long`, y la integridad se garantiza en el código (validando contra `CatalogApi`) en lugar de con claves foráneas.
- **Un evento de dominio como punto de extensión.** `catalog` publica un `StockDecreasedEvent`, y `order` publica un `OrderPaidEvent` que dispara el correo de confirmación, desacoplando los efectos secundarios de la transacción principal.
- **Subpaquetes por área.** Los módulos que han crecido (como `order`, dividido en `cart/` y `order/`) se subdividen para que ningún paquete se convierta en un cajón de sastre.

### Estructura de módulos

```
com.fabio.perfumeshop_api
├── catalog/     Catálogo de perfumes, notas olfativas (pirámide de fragancia)
│   ├── api/         CatalogApi, CatalogItem, CatalogRecommendationItem,
│   │                StockDecreasedEvent, InsufficientStockException
│   └── internal/    Perfume, Note, PerfumeNote, servicios, controller, mapper
├── user/        Registro, login JWT, roles (USER / ADMIN)
│   ├── api/         UserApi
│   └── internal/    auth/ (JWT, filtro, seguridad) · user/ (dominio) · config/
├── order/       Carrito, checkout transaccional, pago simulado, historial
│   └── internal/    cart/ · order/ · exception/
├── chat/        Asistente de compra con IA (SDK de Anthropic)
│   └── internal/    ChatController, ChatService, dto/
└── shared/      GlobalExceptionHandler (transversal)
```

## Diseño por capas

Dentro de cada módulo: los **controllers** traducen HTTP y delegan, los **services** contienen la lógica de negocio y las transacciones, los **repositories** gestionan la persistencia, y los **mappers** (MapStruct) convierten entre entidades y DTOs inmutables (records). La lógica de negocio nunca vive en un controller; las invariantes viven en la entidad, junto al dato que protegen.

## Funcionalidades

- **Catálogo** — CRUD completo (escritura restringida a `ADMIN`), con una **pirámide de fragancia** al estilo Fragrantica: cada perfume se relaciona con notas reutilizables a través de una entidad de asociación `PerfumeNote` que lleva el nivel de la pirámide (salida / corazón / fondo).
- **Autenticación** — JWT sin estado, contraseñas cifradas con BCrypt, roles `USER` / `ADMIN`. El id del usuario se obtiene siempre del token, nunca de la petición.
- **Carrito y pedidos** — gestión del carrito, checkout transaccional (descuenta stock y congela precios), un paso de pago simulado (`PENDING` → `PAID`) e historial de pedidos.
- **Correo de confirmación de pedido** — al pagar, se publica un `OrderPaidEvent` y un `@TransactionalEventListener(AFTER_COMMIT)` envía un correo HTML (plantilla Thymeleaf), solo si la transacción de pago se confirmó de verdad, de modo que un fallo en el envío nunca rompe un pedido ya pagado.
- **Asistente de IA** — un endpoint `/api/chat` respaldado por el SDK de Java de Anthropic. El catálogo actual (nombres, marcas, familias, notas) se inyecta en el system prompt, de forma que Claude recomienda perfumes reales de la tienda. La memoria de la conversación se gestiona reenviando el historial de mensajes en cada llamada.

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/auth/register` · `/login` | Registro / login (devuelve JWT) |
| `GET`  | `/api/perfumes` · `/{id}` | Consultar catálogo (público) |
| `POST`/`PUT`/`DELETE` | `/api/perfumes` | Gestionar catálogo (ADMIN) |
| `GET`/`POST`/`PUT`/`DELETE` | `/api/cart` · `/items` | Gestionar carrito (auth) |
| `POST` | `/api/orders/checkout` · `/{id}/pay` | Checkout y pago (auth) |
| `GET`  | `/api/orders` | Historial de pedidos (auth) |
| `POST` | `/api/chat` | Asistente de IA (auth) |

## Puesta en marcha

Requiere **Java 21**, **Maven** y **PostgreSQL** corriendo en `localhost:5432` con una base de datos llamada `perfumeshop-api`.

La configuración y los secretos se leen de variables de entorno (con valores por defecto para desarrollo donde es seguro):

| Variable | Uso |
|----------|-----|
| `DB_USER`, `DB_PASSWORD`, `DB_NAME` | Credenciales de la base de datos |
| `JWT_SECRET` | Clave de firma del JWT |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP (p. ej. Mailtrap en desarrollo) |
| `ANTHROPIC_API_KEY` | Clave de la API de Anthropic para el chat |

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `/swagger-ui.html`

Flyway crea y puebla el esquema al arrancar. Para crear un administrador, promociona un usuario en la base de datos (`UPDATE users SET role = 'ADMIN' WHERE email = '…'`) y vuelve a iniciar sesión.

## Decisiones de diseño

- DTOs inmutables (`record`); la entidad JPA nunca se expone.
- `BigDecimal` para el dinero; Flyway es dueño del esquema (Hibernate solo valida).
- Los pedidos congelan nombre y precio en el momento de la compra (registro histórico inmutable).
- Errores como `ProblemDetail` (RFC 7807); cada módulo gestiona sus propias excepciones.
- Los secretos nunca van hardcodeados: toda la configuración sensible viene de variables de entorno.

## Estado

Catálogo, usuarios, carrito, pedidos, notas, correo transaccional y el asistente de IA están implementados. La cobertura de tests está en progreso. El despliegue es el siguiente paso.
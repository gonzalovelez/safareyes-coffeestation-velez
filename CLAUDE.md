# Safareyes Coffee Station

Proyecto integrado backend (2º DAW), caso 1 del ecosistema SafaReyes: API REST de la cafetería
CoffeeStation (carta, pedidos en barra y cupones). Trabajo individual.

Enunciado completo en `docs/` (PDF): instrucciones comunes (secciones 1-4), caso CoffeeStation
(sección 5) y contratos de integración (sección 9). Ante cualquier duda, manda el PDF.

## Estado actual

- Esqueleto de Spring Boot generado con Maven: `CoffestationApplication`, `application.properties`
  y un test vacío. Aún no hay entidades, `compose.yaml`, `schema.sql` ni `data.sql`.
- Paquete raíz actual: `es.safareyes.coffestation` (el enunciado propone `es.safareyes.coffeestation`).

## Stack (obligatorio según el enunciado)

- Java 21 y Spring Boot 3.x. **Ojo:** el `pom.xml` actual usa Java 25 y Spring Boot 4.1.1;
  pendiente decidir si se ajusta o se pide autorización al profesor.
- PostgreSQL 16 con Docker Compose (`compose.yaml` en la raíz).
- Spring Data JPA + Hibernate, Jakarta Validation, Lombok, MapStruct, Spring Security 6 + JWT (JJWT),
  `RestClient`, springdoc-openapi (Swagger UI en `/swagger-ui.html`).
- MapStruct, JJWT y springdoc se añaden a mano. Lombok debe ir antes que MapStruct en
  `annotationProcessorPaths` (con `lombok-mapstruct-binding`).
- Colección Postman o Bruno exportada en el repositorio.

## Arquitectura y paquetes

```
es.safareyes.<paquete>
├── config      RestClient, OpenAPI, propiedades
├── controller  @RestController
├── dto/request y dto/response   records
├── entity      @Entity y enums
├── exception   excepciones propias + @RestControllerAdvice
├── mapper      @Mapper (MapStruct)
├── repository  JpaRepository
├── security    SecurityConfig, filtro JWT, filtro API key, UserDetailsService
├── service     interfaces + implementaciones
└── client      cliente RestClient hacia FitZone
```

- Un controlador nunca usa repositorios ni devuelve entidades: recibe y devuelve DTOs.
- Toda la lógica de negocio en servicios; las escrituras con `@Transactional`.
- Inyección por constructor (`@RequiredArgsConstructor` + `final`). Prohibido `@Autowired` en atributos.
- El servicio no conoce URLs ni cabeceras de la API externa: eso vive en `client`.

## Convenciones

- Idioma de trabajo: español.
- Tablas y columnas en `snake_case`, tablas en plural (`lineas_pedido`). Clases `PascalCase`, atributos `camelCase`.
- Rutas bajo `/api/v1`, sustantivos en plural, minúsculas con guiones (`/informes/ventas-dia`).
- Dinero con `BigDecimal` / `NUMERIC(10,2)`, nunca `double`. Fechas `LocalDate`/`LocalDateTime`, zona `Europe/Madrid`.
- Secretos (BD, JWT, API keys) solo en variables de entorno.
- Commits pequeños y descriptivos.

## Requisitos técnicos

- **BD:** mínimo 6 tablas de dominio + `usuarios` (y `roles` si hace falta); 3FN (justificar en el README
  la desnormalización del precio en la línea); al menos una 1:N, una N:M y una FK opcional; PK, FK con
  `ON DELETE`, `UNIQUE`, `NOT NULL`, `CHECK`; un índice manual; `data.sql` con ≥10 filas en tablas
  principales y casos límite (agotados, caducados, inactivos).
- **Entidades:** solo `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`,
  `@ToString(exclude=…)`. Prohibido `@Data`. Enums con `EnumType.STRING`. Relaciones `LAZY`.
  Al menos una entidad con `creadoEn`/`actualizadoEn` automáticos. `@Slf4j` en servicios y controladores.
- **Consultas (mín. 16, todas usadas desde un servicio, tabla en el README):** Q1 derivadas ×5,
  Q2 JPQL con `@Param` ×3 (una con JOIN, una con agregado + GROUP BY), Q3 nativas ×2 (DATE_TRUNC,
  EXTRACT, COALESCE…), Q4 `@Modifying(clearAutomatically = true)` ×1, Q5 proyecciones ×2 (interfaz y
  `SELECT new`), Q6 paginación ×1, Q7 `Specification` ×1, Q8 `@EntityGraph`/`JOIN FETCH` ×1.
- **DTOs:** records; DTO de creación, respuesta y actualización; validaciones solo en DTOs; al menos un
  validador propio (p. ej. DNI o código de cupón). Mappers con `@Mapping` de campos anidados, `ignore`,
  `@MappingTarget` + `NullValuePropertyMappingStrategy.IGNORE`, colecciones y `uses`.
- **Seguridad:** JWT sin estado (60 min), BCrypt, `POST /api/v1/auth/registro` y `/login` públicos.
  Reglas por ruta + `@PreAuthorize` en ≥3 métodos. Un CLIENTE solo ve sus pedidos. CSRF desactivado.
  `/api/v1/integracion/**` con cabecera `X-API-KEY` (filtro propio, clave en variable de entorno).
- **Errores:** `ProblemDetail` desde un único `@RestControllerAdvice`, ≥4 excepciones propias.
  Códigos: 201 con `Location`, 204, 400 (con campos), 401, 403, 404, 409 duplicado/estado, 422 regla de
  negocio, 503 solo en el cliente de integración.
- **Calidad:** README (arranque con `docker compose up -d` + `./mvnw spring-boot:run`, variables de
  entorno, E/R, usuarios de prueba, tabla de consultas, decisiones). Tests recomendados
  (`@DataJpaTest`, JUnit 5 + Mockito).

## Dominio CoffeeStation

**Roles:** `ADMIN` (todo), `BARISTA` (pedidos, disponibilidad, validar cupones), `CLIENTE` (carta, sus
pedidos, validar cupones).

**Entidades mínimas:** Categoria, Producto, Alergeno (N:M con Producto), Pedido, LineaPedido, Cupon,
Usuario. Un pedido tiene cupón y cliente opcionales.

**Reglas de negocio:**
- RN-01 Producto dado de baja (lógica): no sale en la carta ni se puede pedir, pero sigue en pedidos antiguos.
- RN-02 Pedir un producto agotado o de baja: 422 indicando cuál.
- RN-03 La línea guarda el precio unitario del momento.
- RN-04 1-15 líneas por pedido, 1-20 unidades por línea; el mismo producto repetido se agrupa.
- RN-05 IVA incluido del 10 %. El pedido guarda subtotal, descuento, total, base (total / 1,10) e IVA, a 2 decimales.
- RN-06 Cupón válido: existe, activo, dentro de vigencia, sin agotar usos, subtotal > importe mínimo y,
  si es personal, email del cliente coincidente. Tipos: porcentaje o importe fijo.
- RN-07 Descuento socio FitZone (10 %) y cupón no se acumulan: se aplica el mayor. Total nunca negativo.
- RN-08 Estados: `PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO` y `PENDIENTE → CANCELADO`; otra
  transición = 422. Cancelar devuelve el uso al cupón.
- RN-09 Turno = pedidos creados ese día + 1 (reinicia cada día).
- RN-10 Los informes solo cuentan pedidos `ENTREGADO`.

**Endpoints de negocio (16):**

| # | Método | Ruta | Roles |
|---|---|---|---|
| 1 | GET | `/api/v1/productos` (paginado, filtros: categoría, texto, precio máx., disponibilidad, sin alérgeno) | Público |
| 2 | GET | `/api/v1/productos/{id}` | Público |
| 3 | POST | `/api/v1/productos` | ADMIN |
| 4 | PUT | `/api/v1/productos/{id}` | ADMIN |
| 5 | PATCH | `/api/v1/productos/{id}/disponibilidad` | ADMIN, BARISTA |
| 6 | DELETE | `/api/v1/productos/{id}` (baja lógica) | ADMIN |
| 7 | GET | `/api/v1/categorias` (con nº de productos activos) | Público |
| 8 | PATCH | `/api/v1/categorias/{id}/disponibilidad` | ADMIN, BARISTA |
| 9 | POST | `/api/v1/pedidos` (`codigoCupon` o `dniSocio` opcionales) | BARISTA, CLIENTE |
| 10 | GET | `/api/v1/pedidos/{id}` | ADMIN, BARISTA, CLIENTE (propio) |
| 11 | GET | `/api/v1/pedidos` (paginado por estado y fecha) | ADMIN, BARISTA, CLIENTE (los suyos) |
| 12 | PATCH | `/api/v1/pedidos/{id}/estado` | ADMIN, BARISTA |
| 13 | POST | `/api/v1/cupones` | ADMIN |
| 14 | POST | `/api/v1/cupones/validar` | Autenticado |
| 15 | GET | `/api/v1/informes/ventas-dia?fecha=` | ADMIN |
| 16 | GET | `/api/v1/informes/productos-top?desde=&hasta=&limite=` | ADMIN |

**Sugerencias de consultas:** Q1 `findByCategoriaIdAndActivoTrue`, `existsByCodigoIgnoreCase`,
`countByFechaHoraBetween` (turno), `findByEstadoOrderByFechaHoraAsc`, `findByNombreContainingIgnoreCase`;
Q2 suma de ventas entre fechas, productos sin un alérgeno (JOIN + NOT IN), pedidos por email; Q3 ranking
con LIMIT, ventas por franja horaria con `EXTRACT(HOUR …)`; Q4 agotar categoría; Q5 `ProductoCartaView`
y `SELECT new …VentasDiaDto(…)`; Q6 endpoints 1 y 11; Q7 filtros del endpoint 1; Q8 `@EntityGraph`
en el endpoint 10.

## Integración (Fase 5)

- Puerto de CoffeeStation: **8081** (ahora `server.port=8080`, pendiente de cambiar).
- URL y API key remotas en `application.yml` desde variables de entorno
  (`SAFAREYES_FITZONE_URL`, `SAFAREYES_FITZONE_API_KEY`). Ahora el proyecto usa `application.properties`.
- `RestClient` con 3 s de conexión y lectura. Tratar por separado: OK, 4xx, 5xx y timeout/sin conexión.
  La operación principal nunca se bloquea; la respuesta incluye un bloque
  `"integracion": {"servicio", "estado", "detalle"}` con estado `OK`, `NO_APLICA`, `RECHAZADO` o `NO_DISPONIBLE`.
- Solo se intercambian datos imprescindibles (RGPD): nunca nombres, teléfonos ni datos de pago.

**Contrato A (expone CoffeeStation, llama CineTown):** `POST /api/v1/integracion/cupones`
- Petición: `{"email", "origen": "CINETOWN", "referencia"}`.
- Respuesta 201: `{"codigo": "CINE-XXXXXX", "email", "tipo": "PORCENTAJE", "valor": 20.00, "validoHasta", "usosMaximos": 1}`.
- CoffeeStation fija el descuento: 20 %, 7 días, 1 uso, cupón personal para ese email.
- Idempotente por `referencia`: si se repite, devuelve el mismo cupón con 200.
- Errores: 400 (email no válido), 401.

**Contrato B (consume CoffeeStation, expone FitZone):** `GET /api/v1/integracion/socios/{dni}/estado`
- Respuesta 200: `{"dni", "socioActivo", "cuotaAlCorriente", "plan"}`.
- Descuento de socio solo si `socioActivo` y `cuotaAlCorriente` son `true`.
- Errores: 400 (DNI mal formado), 401, 404 (no es socio). Si FitZone no responde, el pedido se crea sin
  descuento y la respuesta lo indica.
- Desarrollar primero contra un simulador (WireMock o perfil `mock`, con un retraso de 5 s para probar el
  timeout). Documentar la prueba real y un caso de fallo en `docs/integracion.md`.

## Fases y entregables

| Fase | Semana | Entregable |
|---|---|---|
| 0 Arranque | 1 | Repo, proyecto Initializr, `compose.yaml`, conexión a PostgreSQL |
| 1 Base de datos | 1-2 | `docs/analisis.md`, diagrama E/R en `docs/`, `schema.sql`, `data.sql`, `docs/consultas.sql` (3 consultas) |
| 2 Persistencia JPA | 3-4 | Entidades, enums, auditoría, 16 consultas probadas; `ddl-auto=validate` |
| 3 API REST | 5-6 | DTOs, mappers, servicios con las RN, 16 endpoints, errores, Swagger |
| 4 Seguridad | 7 | Usuarios, JWT, roles, propiedad del recurso, filtro API key |
| 5 Integración | 8-9 | Contrato A expuesto, cliente del contrato B, `docs/integracion.md` |
| 6 Cierre | 10 | README, colección Postman/Bruno, repaso de requisitos |

## Git y GitHub

- Remoto `origin`: https://github.com/gonzalovelez/safareyes-coffeestation-velez
  (pendiente confirmar si debe apuntar a `gvelezrincon-tech`, como decía la versión anterior).
- Se trabaja y se hace push con la cuenta `gonzalovelez`.
- Ramas: `main` (principal) y `development` (trabajo; se integra aquí antes de pasar a `main`).

## Documentación

- Los documentos del proyecto van en `docs/`.

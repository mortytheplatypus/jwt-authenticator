# Request lifecycle (Spring Security + JWT)

Stateless API: no HTTP session, CSRF off. Each request is authenticated from the `Authorization: Bearer <jwt>` header, or not at all.

## Chain (this app)

Servlet container → Spring Security `FilterChainProxy` → `DispatcherServlet` (only if security lets the request through).

Relevant filters, in order:

1. **CORS** — `http://localhost:3000`, credentials allowed. Preflight `OPTIONS` is handled here.
2. **SecurityContextHolderFilter** — empty context per request (`STATELESS`).
3. **`JwtAuthFilter`** — custom; runs **before** `UsernamePasswordAuthenticationFilter`.
4. **`UsernamePasswordAuthenticationFilter`** — unused for login. Login is a normal controller call, not form login.
5. **`ExceptionTranslationFilter`** — turns authz failures into JSON 401/403.
6. **`AuthorizationFilter`** — applies the rules in `SecurityConfig`.

Then MVC: controller → service → `GlobalExceptionHandler` if something is thrown.

## Authorization rules

| Matcher | Rule |
|---|---|
| `/auth/**` | `permitAll` |
| `/admin/**` | must be authenticated **and** have authority `ADMIN` |
| everything else | must be authenticated |

`User.getAuthorities()` is `SimpleGrantedAuthority(role.name())` — `ADMIN` or `USER`.

## `JwtAuthFilter`

Always calls `filterChain.doFilter`. It never writes a response.

1. Read `Authorization`. If it is missing or not `Bearer …`, skip.
2. If `SecurityContext` already has an `Authentication`, skip.
3. Parse JWT (`JwtService.extractUsername` verifies signature).
4. Load user from DB (`UserDetailsServiceImpl`).
5. `isValid`: not expired **and** `sub` equals DB username.
6. On success: put `UsernamePasswordAuthenticationToken` (principal = `User`, authorities from DB) on the context.
7. On `JwtException` or `UsernameNotFoundException`: clear context, continue.

Role in the JWT payload is **not** used for authorization. Authorities come from the database.

Invalid/tampered/expired tokens therefore look the same as “no token” by the time authorization runs.

## After the filter: AuthorizationFilter

- **Public (`/auth/**`)** — proceeds even with empty context.
- **Protected, no authentication** — `AuthenticationException` → `AuthenticationRequiredEntryPoint` → **401** `{ "success": false, "message": "Authentication required" }`.
- **Authenticated, wrong role** (`USER` hitting `/admin/**`) — `AccessDeniedException` → `AuthorizationDeniedHandler` → **403** `{ "success": false, "message": "Access denied" }`.
- **Authenticated, allowed** — request reaches the controller.

There is currently no controller under `/admin` or other protected paths; the rules still apply if you add them.

## Login / register (`POST /auth/login`, `POST /auth/register`)

Both are permit-all. They still pass through `JwtAuthFilter` (usually a no-op).

**Register**

- Bean validation fails → **400** (`GlobalExceptionHandler`).
- Email or username already exists → **400**.
- Else: hash password (BCrypt), save with role `USER` → **201**.

**Login**

- Bean validation fails → **400**.
- Controller calls `AuthenticationManager.authenticate(username, password)` → `DaoAuthenticationProvider` → `UserDetailsServiceImpl` + `BCryptPasswordEncoder`.
- Bad username/password → `BadCredentialsException` → **401** `{ "token": null }`.
- Success → `JwtService.generateToken` (`sub` = username; claims `name`, `email`, `username`, `role`; TTL from `application.yml`) → **200** `{ "token": "…" }`.

## Outcomes

| Request | Result |
|---|---|
| Valid Bearer JWT, allowed path | Context set → controller |
| Valid JWT, `/admin/**`, role `USER` | **403** Access denied |
| Missing / bad / expired JWT, protected path | **401** Authentication required |
| Same, `/auth/**` | Controller runs |
| Login, wrong password | **401** `{ "token": null }` |
| Register, duplicate / invalid body | **400** |

Client stores the token and sends `Authorization: Bearer <token>` on later requests. Server does not persist sessions.

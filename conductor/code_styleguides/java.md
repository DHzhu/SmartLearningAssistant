# Java Code Style Guide - Smart Learning Assistant

## 1. General Rules
- Follow official Oracle/Google Java Coding Conventions.
- Embracing Java 21: Use virtual threads (Loom) for high throughput. Use `records` for DTOs and immutable data structures.
- Embrace modern Switch Pattern Matching and Pattern Matching for `instanceof` where appropriate.
- Use Text Blocks for multi-line strings (SQL, JSON, Lua scripts).

## 2. Naming Conventions
- **Classes/Types/Records**: PascalCase (e.g. `BillingService`, `QuotaDto`)
- **Functions/Variables**: camelCase (e.g. `deductToken`, `userId`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g. `REDIS_KEY_PREFIX`)
- **Packages**: lowercase (e.g. `com.example.assistant.billing`)

## 3. Language Features & Best Practices
- **Null Safety**: Prefer `Optional` for return types that may be absent. Annotate with `@NonNull` and `@Nullable` where appropriate.
- **Concurrency**: Leverage Spring's `@Async` and `@Scheduled`. Always ensure resource cleanups inside `try-with-resources`.
- **Immutability**: Make classes immutable by default; prefer `record` or `final` fields.

## 4. Spring & Database Standards
- **Authentication Security**: Apply `@PreAuthorize` method-level constraints on Admin endpoints.
- **RAG & Vector Retrieval**: Always ensure the active query filter includes `WHERE user_id = ?` to maintain multi-tenant data boundary.
- **Lua Database Billing Operations**: Encapsulate transactional Redis quota checks and deducts in Lua scripts to prevent overselling.

## 5. Testing Specifics
- **Framework**: Use JUnit 5 and AssertJ.
- **Mocking**: Use `@Mock` and Mockito for service-level testing. Avoid mocking database layers; use Testcontainers for PostgreSQL/pgvector and Redis integration tests when possible.

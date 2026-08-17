# Track Specification: Long-term Memory & Persona

## 1. Goal
Implement a persistent Long-term Memory and Learner Persona system to dynamically extract, store, and inject user learning profiles and weak spots into AI prompt contexts.

## 2. User Stories
- As a student, I want the AI tutor to remember my weak subjects (e.g. "Recursion in Data Structures") and target exam goals across different chat sessions.
- As a student, I want to view and manage my stored profile memory facts in my settings.

## 3. Technical Strategy
- **Core Approach**:
  - `UserMemory` Entity: Persisted in PostgreSQL with `userId`, `category` (PREFERENCE, WEAKNESS, GOAL, FACT), `content`, and `importance`.
  - `MemoryExtractionService`: Analyzes conversation messages for learner intents and auto-extracts persistent memory items.
  - `UserPersonaService`: Manages user memory lifecycle and formats persona prompts for injection into `RagService` and `AgenticChatService`.
  - `MemoryController`: REST endpoints (`GET /api/memory`, `POST /api/memory`, `DELETE /api/memory/{id}`).
- **Key Dependencies**: Spring Data JPA.

## 4. Constraints & Standards
- Multi-tenant isolation: Memory queries must strictly be filtered by authenticated `userId`.
- Test coverage >80%.

## 5. Success Criteria
- [x] UserMemory entity and repository created.
- [x] Memory extraction accurately detects learning goals and weak points.
- [x] Persona injected into RAG & Agent prompt context.
- [x] All tests pass with >80% coverage.


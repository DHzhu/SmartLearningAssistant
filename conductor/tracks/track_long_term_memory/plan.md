# Implementation Plan: Long-term Memory & Persona
 
## 1. Research & Analysis
- [x] **task-mem-1**: Research Memory extraction and Persona injection patterns. [SHA: ] (c8f685d)
- [x] **task-mem-2**: Document Memory entity and API contracts in `spec.md`. [SHA: ] (c8f685d)
 
## 2. Preparation & Foundation
- [x] **task-mem-3**: Create unit tests for `UserPersonaServiceTest` and `MemoryExtractionServiceTest` (TDD Red). [SHA: ] (bfb10f4)
- [x] **task-mem-4**: Create `UserMemory` entity, Flyway migration, and `UserMemoryRepository`. [SHA: ] (bfb10f4)
 
## 3. Implementation
- [x] **task-mem-5**: Implement `MemoryExtractionService`, `UserPersonaService`, and `MemoryController`. [SHA: ] (bfb10f4)
- [x] **task-mem-6**: Integrate persona injection into `RagService` and `AgenticChatService`. [SHA: ] (bfb10f4)
 
## 4. Verification & Hardening
- [ ] **task-mem-7**: Verify 100% test pass and >80% coverage. [SHA: ]
- [ ] **task-mem-8**: Multi-tenant memory data isolation verification. [SHA: ]
- [ ] **task-mem-9**: Code Review: Check indexing and prompt token overhead. [SHA: ]
 
## 5. Track Closure & Archiving
- [ ] **task-mem-10**: Follow Archive Protocol from `workflow.md` Section 5:
    - [ ] Run `./mvnw clean package -DskipTests` to verify project stability. [SHA: ]
    - [ ] Update `CHANGELOG.md` (in Chinese). [SHA: ]
    - [ ] Set `metadata.json` status to "done" and rename ID. [SHA: ]
    - [ ] Move folder to `conductor/archive/`. [SHA: ]
    - [ ] Update `conductor/tracks.md` and move entry to Archive section. [SHA: ]
    - [ ] Execute final archiving commit. [SHA: ]


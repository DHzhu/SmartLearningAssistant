# Implementation Plan: Agentic Workflow & Tool Calling

## 1. Research & Analysis
- [x] **task-agentic-1**: Investigate Spring AI 2.0 Function Calling and Tool APIs. [SHA: ] (1f49b80)
- [x] **task-agentic-2**: Document agentic architecture and tool schemas in `spec.md`. [SHA: ] (1f49b80)

## 2. Preparation & Foundation
- [x] **task-agentic-3**: Create unit tests for tools (AccountToolTest, StudyPlannerToolTest) (TDD Red). [SHA: ] (a46b2fe)
- [x] **task-agentic-4**: Implement tool components (`AccountTool`, `KnowledgeSearchTool`, `StudyPlannerTool`). [SHA: ] (a46b2fe)

## 3. Implementation
- [x] **task-agentic-5**: Implement `AgenticChatService` with Spring AI tool routing and fallback. [SHA: ] (db5cffd)
- [x] **task-agentic-6**: Integrate `AgenticChatService` into Controller endpoints and Frontend API. [SHA: ] (db5cffd)

## 4. Verification & Hardening
- [x] **task-agentic-7**: Verify 100% test pass and >80% coverage for agentic module. [SHA: ] (4f03637)
- [x] **task-agentic-8**: Manual & integration validation of tool execution and token deduction. [SHA: ] (4f03637)
- [x] **task-agentic-9**: Code Review: Check multi-tenant tool safety and style compliance. [SHA: ] (4f03637)

## 5. Track Closure & Archiving
- [ ] **task-agentic-10**: Follow Archive Protocol from `workflow.md` Section 5:
    - [ ] Run `./mvnw clean package -DskipTests` to verify project stability. [SHA: ]
    - [ ] Update `CHANGELOG.md` (in Chinese). [SHA: ]
    - [ ] Set `metadata.json` status to "done" and rename ID. [SHA: ]
    - [ ] Move folder to `conductor/archive/`. [SHA: ]
    - [ ] Update `conductor/tracks.md` and move entry to Archive section. [SHA: ]
    - [ ] Execute final archiving commit. [SHA: ]


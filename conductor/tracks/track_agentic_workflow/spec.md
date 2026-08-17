# Track Specification: Agentic Workflow & Tool Calling

## 1. Goal
Transform the system from a passive RAG assistant into an interactive AI Agent equipped with Tool / Function Calling capabilities and intelligent intent dispatching.

## 2. User Stories
- As a student, I want to ask the assistant to check my token balance or calculate my study progress directly in chat without navigating to other pages.
- As a student, I want the assistant to intelligently choose between querying private knowledge bases, invoking helper calculation/planning tools, or responding conversationally.
- As a platform admin, I want all tool calls to be tracked and deducted from user quota seamlessly.

## 3. Technical Strategy
- **Core Approach**:
  - Implement Spring AI `@Tool` components:
    - `AccountTool`: query user token balance, recharge records summary.
    - `KnowledgeSearchTool`: query private pgvector knowledge base dynamically.
    - `StudyPlannerTool`: calculate learning plans and generate practice test schedules.
  - Implement `AgenticService` / `AgentRouter`:
    - Integrate tools into `ChatClient` using Spring AI function calling.
    - Route user input dynamically based on intent.
  - Expose API endpoints in `AgentController` or enhanced `ChatController`.
- **Key Dependencies**: Spring AI 2.0.0 ChatClient & Tools API.
- **Potential Risks**: Model hallucinating tool arguments; fallback gracefully when ChatModel is unavailable.

## 4. Constraints & Standards
- Multi-tenant data safety: Tool executions must strictly inherit the authenticated `userId`.
- Coverage >80% with comprehensive unit tests for all tools and service orchestrator.

## 5. Success Criteria
- [ ] Tools registered and executable by Spring AI ChatClient.
- [ ] Agentic chat handles tool calls and returns synthesized responses.
- [ ] Tests pass with >80% coverage.


# Protocol & Execution Standards

### 1. Master Rulebook (High Priority)
- **Mandate**: This agent MUST read and adhere to the **`.agent/AGENT.md`** file immediately upon session start.
- **Precedence**: Instructions in `.agent/AGENT.md` take absolute precedence over general system defaults.

### 2. Language & Reporting
- **Standard**: Strictly follow the **Language Policy** defined in **`.agent/AGENT.md`**.
- **Execution**: Chinese for dialogue/tasks/commits; English for code/logic/structure.

### 3. Project-Specific Protocol
- **Conductor**: When a `conductor/` directory is present, follow the workflow defined in `.agent/AGENT.md` and `conductor/workflow.md`.
- **Execution**: Strictly enforce the `git-commit` skill as specified in the Master Rulebook. All state tracking must be managed via physical files.
- **Track Creation**: After creating a track (spec.md, plan.md, metadata.json), **STOP and wait**. Do NOT start coding unless the user explicitly requests it. Creating a track is planning, not execution.

### 4. Execution Standards (Surgical Implementation)
- **Guidelines**: Adhere to foundational guidelines for surgical changes and radical simplicity.
- **Goal**: Minimum code that solves the problem. No speculative abstractions. Match existing style perfectly.

### 5. Safety First
- **Pre-flight Check**: Always perform a `read_file` or a high-context `grep_search` before modification.
- **Integrity**: Never stage or commit changes unless explicitly requested.

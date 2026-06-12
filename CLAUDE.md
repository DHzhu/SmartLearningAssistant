# Coordination Protocol

## 1. Master Rulebook
- **Source of Truth**: This agent MUST read and strictly adhere to **`.agent/AGENT.md`** for all operations.
- **Bootstrapping**: Upon session start, follow the "Universal File Resolution Protocol" through `conductor/index.md`.

## 2. Governance
- **Lifecycle**: For all track-level changes, maintain state in physical files (`tracks.md`, `plan.md`).
- **Tasks**: Follow the workflow defined in `.agent/AGENT.md` and `conductor/workflow.md`.
- **Commits**: For code changes, invoke `activate_skill("git-commit")`.
- **Track Creation**: After creating a track (spec.md, plan.md, metadata.json), **STOP and wait**. Do NOT start coding unless the user explicitly requests it. Creating a track is planning, not execution.

## 3. Standards
- **Surgical Implementation**: Follow guidelines for surgical precision and radical simplicity. Aim for minimum code that solves the problem.
- **Language**: Strictly follow the **Language Policy** defined in **`.agent/AGENT.md`**.
- **Quality**: Enforce >80% coverage and no security leaks.
- **Safety**: High-context `grep_search` is acceptable for "pre-flight" checks.

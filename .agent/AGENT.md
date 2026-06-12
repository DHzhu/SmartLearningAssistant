# Agent Master Protocol - Smart Learning Assistant

## 1. Identity & Context
- **Bootstrap**: Agent **MUST** locate `conductor/index.md` on session start. Use "Universal File Resolution Protocol" to resolve project docs.
- **Precedence**: `AGENT.md` defines **HOW** (Principles); `index.md` defines **WHAT** (Context/CLI).

## 2. Core Mandates
- **Robust Coding Principles**:
  - **Surgical Precision**: Touch only what you must. Do not "improve" adjacent code, comments, or formatting. Clean up only your own mess.
  - **Radical Simplicity**: Write the minimum code that solves the problem. No speculative features, abstractions, or "flexibility" that wasn't requested.
  - **Explicit Clarity**: Never assume. Surface assumptions explicitly. If something is unclear, stop and ask.
- **Security**: **NEVER** log/commit secrets. Protect config/system folders.
- **Language Policy**:
  - **Chinese**: All user dialogue, git commit messages, git notes, `README.md`, and `CHANGELOG.md`.
  - **English**: All code, technical logic/strategies, operational rules, and process documentation (e.g., `plan.md`, `spec.md`, `tracks.md`, `workflow.md`). Document structures (headers/keys) remain English.
- **Integrity**: All progress **MUST** be tracked in `plan.md`. Agent **MUST STOP** and wait for Directive after Track initialization.

## 3. Governance & Workflow
- **Lifecycle**: Agent **MUST** follow `workflow.md` for all tasks. All progress and state tracking **MUST** be maintained in physical files (`tracks.md`, `plan.md`, `metadata.json`).
- **Prep**: Perform "pre-flight" check (read_file/grep_search) before any modifications.
- **Compliance**: Newly created files MUST strictly adhere to the requirements in this specification. Historical archives may contain errors; imitating them is prohibited.
- **Commits**: Code changes **MUST** use `activate_skill("git-commit")`. Format **MUST** be: `<type>(<scope>): <Chinese description>`. Body text, `Co-Authored-By`, or feature lists are **STRICTLY FORBIDDEN**.

## 4. Quality Gates (DoD)
A task is **DONE** ONLY when:
1. **Standards**: **MUST** pass project verification (Build/Test/Lint), TDD verified, and Coverage >80%.
2. **Audit**: **MUST** be committed with Git Notes and SHA synchronized in `plan.md`.
3. **Integrity**: Physical files (`tracks.md` and `plan.md`) must accurately reflect the final state of the track and its tasks.

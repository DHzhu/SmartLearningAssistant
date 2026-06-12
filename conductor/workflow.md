# Project Operations Manual (Automated & Standardized) - Smart Learning Assistant

## 1. Guiding Principles
- **Master Rulebook**: All operations are governed by **`.agent/AGENT.md`**.
- **The Plan is Truth**: Every action must be tracked in `plan.md`.
- **The Spec is the Source of Truth**: Findings from Research MUST be directly integrated into **`spec.md`**. **No fragmented `research.md` files.**
- **Tech Stack Integrity (Critical)**: If the implementation deviates from the established `tech-stack.md`:
  1. **STOP** implementation.
  2. Update `tech-stack.md` with the new design and a dated explanation.
  3. **RESUME** implementation only after the documentation is updated.

## 2. Track Initialization (Three-Asset Protocol)
When starting a new Track:
1. **Create Folder**: `conductor/tracks/<track_id>/`.
2. **Copy Templates**: Populate the folder with standard templates from `conductor/templates/`:
   - `metadata.json` (Set `"status": "in_progress"`)
   - `spec.md` (Define goal and technical strategy)
   - `plan.md` (Draft tasks and sub-tasks)
3. **Registry**: Add the new track entry to `conductor/tracks.md` under **Active Tracks**, strictly following the **Standard Entry Format** defined in that file.
4. **Closing Task**: The initialization MUST ensure the `plan.md` includes the "Track Closure & Archiving" section as defined in the template. This section is the MANDATORY final step for every track.

## 3. Standard Task Workflow (Continuous Delivery)

1. **Select & Research**: Choose the next task from `plan.md`.
   - **Research Sync (Critical)**: Findings MUST be directly synced into `spec.md` (Technical Strategy) and `plan.md` (Task Refinement). **For major architectural shifts, STOP and inform the user.**
2. **Mark In Progress**: Change task status to `[~]` in `plan.md`.
3. **TDD (Red-Green-Refactor)**: 
   - **Red**: Write a failing test for the expected behavior.
   - **Green**: Write minimum code to pass the test.
   - **Refactor**: Optimize while maintaining test success.
4. **Quality Gates**:
   - **Coverage**: New code MUST meet project target (Default: **>80%**).
   - **Checks**: Execute `./mvnw compile` for backend, and `npm run lint` && `npx tsc --noEmit` (in frontend dir) for frontend.
5. **Skill-based Delivery (git-commit)**: 
   - **Trigger**: Agent **MUST** trigger this when a **logical milestone** is reached OR a maximum of **3 sub-tasks** are completed but not yet committed.
   - **Action**: Invoke `activate_skill("git-commit")`. This tool will update `plan.md` with SHAs and mark tasks as `[x]`.

## 4. Context-Aware Quality Gates (Auto-Validation)
*The Agent must self-verify all requirements before delivery:*
- [ ] **Functional**: Tests pass, requirements met (both backend & frontend).
- [ ] **Coverage**: >80% for new code via backend `./mvnw jacoco:report` and frontend `npm run coverage`.
- [ ] **Standards**: JSDoc/KDoc for public APIs; 100% type safety for Java and TypeScript.
- [ ] **Security**: No hardcoded secrets; Zero new vulnerabilities.
- [ ] **Code Review**: Self-audit for logic, naming, and idiomatic quality.
- [ ] **Project Guidelines**: Verify compliance with `conductor/product-guidelines.md`.

## 5. Track Implementation & Archiving
1. **Integrity Check**: Execute backend build `./mvnw clean package -DskipTests` and frontend build `npm run build` to verify project stability.
2. **Auto-Changelog**: Update `CHANGELOG.md` following [Keep a Changelog](https://keepachangelog.com/).
3. **Archive Protocol**:
   - **Metadata**: Set `"status": "done"`. Update `id` to `track_id_YYYYMMDD` format.
   - **Move**: Folder from `conductor/tracks/` to `conductor/archive/`.
   - **Registry**: Move entry to **Archive** in `conductor/tracks.md`.
   - **Commit**: Final cleanup commit: `chore(archiving): Archive completed track <track_id>`.

## 6. Development Commands
### Backend
- **Build**: `./mvnw clean package -DskipTests`
- **Test**: `./mvnw test`
- **Coverage**: `./mvnw jacoco:report`
- **Static Analysis**: `./mvnw compile`

### Frontend (Run in frontend directory)
- **Build**: `npm run build`
- **Test**: `npm run test`
- **Coverage**: `npm run coverage`
- **Static Analysis**: `npm run lint` && `npx tsc --noEmit`

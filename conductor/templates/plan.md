# Implementation Plan: {{TITLE}}

## 1. Research & Analysis
- [ ] Investigate existing implementations and dependencies. [SHA: ]
- [ ] Document technical strategy in `spec.md`. [SHA: ]

## 2. Preparation & Foundation
- [ ] Initialize testing environment and create failing tests (TDD Red). [SHA: ]
- [ ] Create core models/interfaces. [SHA: ]

## 3. Implementation
- [ ] Build primary logic/service layer. [SHA: ]
- [ ] Integrate with existing system. [SHA: ]

## 4. Verification & Hardening
- [ ] Verify 100% test pass and >80% coverage. [SHA: ]
- [ ] Final security audit and manual validation. [SHA: ]
- [ ] Code Review: Final audit for logic, style, and maintainability. [SHA: ]

## 5. Track Closure & Archiving
- [ ] Follow Archive Protocol from `workflow.md` Section 5:
    - [ ] Run `./mvnw clean package -DskipTests` to verify project stability. [SHA: ]
    - [ ] Update `CHANGELOG.md` (in Chinese). [SHA: ]
    - [ ] Set `metadata.json` status to "done" and rename ID. [SHA: ]
    - [ ] Move folder to `conductor/archive/`. [SHA: ]
    - [ ] Update `conductor/tracks.md` and move entry to Archive section. [SHA: ]
    - [ ] Execute final archiving commit. [SHA: ]

# Implementation Plan: Advanced RAG & Reranker

## 1. Research & Analysis
- [x] **task-rag-1**: Research Reranker scoring models and semantic chunking strategies. [SHA: ] (37e591c)
- [x] **task-rag-2**: Document Advanced RAG architecture in `spec.md`. [SHA: ] (37e591c)

## 2. Preparation & Foundation
- [x] **task-rag-3**: Create unit tests for `SemanticChunkerTest` and `RerankerServiceTest` (TDD Red). [SHA: ] (120c085)
- [x] **task-rag-4**: Implement `SemanticChunker` and `RerankerService`. [SHA: ] (120c085)

## 3. Implementation
- [x] **task-rag-5**: Integrate Reranker and SemanticChunker into `RagService` and `VectorizationService`. [SHA: ] (5baa3f6)
- [x] **task-rag-6**: Update tests and verify two-stage candidate retrieval. [SHA: ] (5baa3f6)

## 4. Verification & Hardening
- [ ] **task-rag-7**: Verify 100% test pass and >80% coverage. [SHA: ]
- [ ] **task-rag-8**: Performance & latency audit for re-ranking. [SHA: ]
- [ ] **task-rag-9**: Code Review: Check memory safety and boundary slicing. [SHA: ]

## 5. Track Closure & Archiving
- [ ] **task-rag-10**: Follow Archive Protocol from `workflow.md` Section 5:
    - [ ] Run `./mvnw clean package -DskipTests` to verify project stability. [SHA: ]
    - [ ] Update `CHANGELOG.md` (in Chinese). [SHA: ]
    - [ ] Set `metadata.json` status to "done" and rename ID. [SHA: ]
    - [ ] Move folder to `conductor/archive/`. [SHA: ]
    - [ ] Update `conductor/tracks.md` and move entry to Archive section. [SHA: ]
    - [ ] Execute final archiving commit. [SHA: ]


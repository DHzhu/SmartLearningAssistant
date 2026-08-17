# Track Specification: Advanced RAG & Reranker

## 1. Goal
Upgrade the retrieval pipeline from naive vector search to an Advanced RAG architecture featuring Semantic Chunking and a Two-Stage Retrieval + Re-ranking pipeline.

## 2. User Stories
- As a learner, I want the AI assistant to accurately retrieve the most relevant sections of study notes without getting lost in low-relevance chunks.
- As a learner, I want documents to be chunked intelligently along natural semantic and paragraph boundaries rather than arbitrary token cuts.

## 3. Technical Strategy
- **Semantic Chunking**:
  - Implement `SemanticChunker` that splits documents based on paragraph markers (`\n\n`), markdown headers, and sentence boundaries while maintaining target chunk size and overlap.
- **Two-Stage Retrieval & Re-ranking**:
  - Implement `RerankerService` using a hybrid relevance scoring algorithm (combining exact keyword frequency, semantic proximity, and position weights).
  - Modify `RagService` to retrieve a broader candidate pool (`Top-M`, e.g. 10) and then apply `RerankerService` to filter down to the most relevant `Top-K` (e.g. 3).
- **Key Dependencies**: Spring AI Document APIs, pgvector.

## 4. Constraints & Standards
- Maintain strict multi-tenant isolation (`userId` filter).
- Re-ranking overhead < 50ms per query.
- Test coverage >80%.

## 5. Success Criteria
- [ ] `SemanticChunker` correctly chunks diverse text types without cutting sentences in half.
- [ ] `RerankerService` accurately re-orders candidates to place highest-relevance documents first.
- [ ] `RagService` integrates re-ranking and tests pass >80% coverage.


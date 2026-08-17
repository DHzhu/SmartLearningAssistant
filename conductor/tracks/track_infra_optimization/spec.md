# Track Specification: Infrastructure Optimization & MQ

## 1. Goal
Decouple heavyweight asynchronous tasks (vector embedding, knowledge ingestion) using an asynchronous event queue architecture and establish an automated reconciliation engine for billing ledger consistency.

## 2. User Stories
- As a user uploading large course documents, I want instant confirmation and decoupled background queue processing without HTTP connection timeouts.
- As a system administrator, I want automatic billing reconciliation between the Redis cache and SQL ledger to prevent quota drifting.

## 3. Technical Strategy
- **Event-Driven Messaging Layer**:
  - `EventMessage<T>`: Standardized message envelope.
  - `AsyncEventQueue`: Resilient producer-consumer event broker supporting retry and dead-letter routing.
  - `KnowledgeTaskConsumer`: Background message listener for vector ingestion.
- **Reconciliation Engine**:
  - `BillingReconciliationService`: Reconciles cached balances against SQL ledger logs and fixes anomalies.
- **Key Dependencies**: Spring Task / Async.

## 4. Constraints & Standards
- Resilient queue processing with error isolation.
- Test coverage >80%.

## 5. Success Criteria
- [ ] `AsyncEventQueue` and `EventMessage` implemented with producer/consumer tests.
- [ ] `BillingReconciliationService` verifies ledger and fixes discrepancy.
- [ ] Full test suite passes with >80% coverage.


# Track Specification: Token 无锁并发计费中枢

## 1. Goal
实现高并发、高可靠的无锁 Token 计费系统。在 AI 对话场景下，通过 Redis Lua 脚本保证“余额查询 -> 余额扣减”的原子性，彻底消灭并发超卖，并支持定时异步将 Redis 账本同步持久化回 PostgreSQL 关系表。

## 2. User Stories
- **As a User**: 我希望在界面右上角看到我的当前 Token 余额，并且每次对话完成后，我的余额能即时刷新，扣费清晰可见。
- **As a System**: 我需要在面对多用户高并发对话请求时，避免因为传统数据库行锁导致的阻塞，同时绝不能发生用户余额为负数的“超卖”现象。

## 3. Technical Strategy
- **Core Approach**:
  - **Redis 高并发防超卖 (Lua Script)**:
    - 用户实时额度数据（配额）首选缓存在 Redis 内存中。
    - 将“读取当前余额 -> 校验余额是否充足 -> 执行扣减”这组原子操作编写为 Lua 脚本，分发至 Redis 执行。由于 Redis 的单线程执行模型，Lua 脚本在执行时不会被其他请求打断，消灭了竞态条件，保证了无锁状态下的强一致性。
  - **定时异步持久化 (`@Scheduled`)**:
    - 使用 Spring Boot 定时任务，每隔固定周期（如 5 分钟或达到触发阈值），将 Redis 中的变更额度批量同步回 PostgreSQL 的 `sys_user_quota` 关系表，以防止 Redis 宕机造成账目丢失。
  - **前端账单看板**:
    - 在 React 前端中开发配额管理与账单看板，展示用户当前剩余 Token、扣费历史记录及充值额度。

## 4. Constraints & Standards
- Redis 扣费的 Lua 脚本必须能够防范各种边缘情况（例如不存在对应 Key 时返回特定错误码）。
- 定时同步回 PG 的过程必须使用批量更新或合并事务处理，最大程度减少对 PG 写入 IOPS 的占用。
- 前端需要优雅展示“余额不足，请充值”的拦截阻断机制。

## 5. Success Criteria
- [ ] 编写高并发压力测试，验证在同一用户多路并发请求扣减额度时，Redis 绝不超卖（且不锁表）。
- [ ] 验证 `@Scheduled` 任务正确触发，Redis 缓存的配额与 PostgreSQL 中 `sys_user_quota` 成功保持同步。
- [ ] 前端看板正确展示 Token 历史变化曲线和充值明细。
- [ ] Java 与 TypeScript 的单元测试覆盖率均大于 80%。

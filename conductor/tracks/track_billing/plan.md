# Implementation Plan: Token 无锁并发计费中枢

## 1. Research & Analysis
- [ ] 调研 Redis 单线程模型下 Lua 脚本执行性能瓶颈及错误恢复机制。 [SHA: ]
- [ ] 制定在高并发情况下批量同步 Redis 账本回 PostgreSQL 的事务处理方案。 [SHA: ]
- [ ] 最终确认 `conductor/tracks/track_billing/spec.md` 技术方案。 [SHA: ]

## 2. Preparation & Foundation
- [ ] 后端引入 Spring Boot Starter Data Redis 依赖，配置 Redis 连接池。 [SHA: ]
- [ ] 数据库创建 `sys_user_quota` 与 `sys_billing_log` 表结构。 [SHA: ]

## 3. Implementation
- [ ] **后端**：编写 Redis Lua 原子扣费脚本，并在 Java 侧封装成 RedisTemplate 脚本执行逻辑。 [SHA: ]
- [ ] **后端**：编写定时同步任务服务，实现 Redis 状态向 PostgreSQL 的批量持久化同步。 [SHA: ]
- [ ] **前端**：在 React 中开发右上角全局 Token 状态卡片组件与配额看板路由页面。 [SHA: ]
- [ ] **前端**：处理 API 接口返回的“余额不足”错误码，在前端进行优雅的充值拦截弹窗提示。 [SHA: ]

## 4. Verification & Hardening
- [ ] 编写并发并发扣减压测用例，使用多线程并发扣费，断言最终扣减总额准确无误。 [SHA: ]
- [ ] 编写单元测试模拟定时任务执行中数据库发生故障时的回滚及重试机制。 [SHA: ]
- [ ] 校验 Checkstyle 与 ESLint 等静态分析规范。 [SHA: ]

## 5. Track Closure & Archiving
- [ ] 按照 `workflow.md` 执行完备归档协议：
    - [ ] 分别在 `backend` 目录下执行 `./mvnw clean package -DskipTests` 和 `frontend` 目录下执行 `npm run build` 确保整体验收无误。 [SHA: ]
    - [ ] 在 `CHANGELOG.md` 中以中文详细记录此特性的变更内容。 [SHA: ]
    - [ ] 将当前轨道的 `metadata.json` 状态修改为 "done"，并追加日期后缀重命名轨道 ID。 [SHA: ]
    - [ ] 将轨道文件夹移动至 `conductor/archive/`。 [SHA: ]
    - [ ] 在 `conductor/tracks.md` 中将当前轨道条目移入归档区。 [SHA: ]
    - [ ] 执行最终的归档 Git 提交。 [SHA: ]

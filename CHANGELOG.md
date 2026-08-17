# 更新日志

所有对本项目的显著变更都将记录在此文件中。

本项目遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范。

## [未发布]

### 新增
- (在此记录新增功能)

### 变更
- (在此记录现有功能的改进)

### 修复
- (在此记录修复的 Bug)

### 移除
- (在此记录已移除的功能)

## [0.7.0] - 2026-08-17

### 新增
- **长期记忆与学习者画像体系 (Long-term Memory & Learner Persona)**：
  - `UserMemory` 实体与 Flyway `V4__create_user_memory.sql` 数据表
  - `MemoryExtractionService`：从学生对话中自动挖掘薄弱点 (WEAKNESS)、学习目标 (GOAL) 与偏好 (PREFERENCE)
  - `UserPersonaService`：个性化画像组装与 Prompt 动态注入
  - `MemoryController`：`/api/memory` 记忆管理 REST API 端点
  - 增强 `RagService`：支持结合画像档案实现千人千面的差异化问答


## [0.6.0] - 2026-08-17

### 新增
- **高阶 RAG 语义切块与重排机制 (Advanced RAG & Reranker)**：
  - `SemanticChunker`：自然段落与句法感知语义切块组件
  - `RerankerService`：两阶段检索混合重排序引擎（关键词词频 + 短语匹配 + 首部权重）
  - `RagService`：升级为两阶段候选召回（Top-10 候选池）+ 重排精筛（Top-3 上下文）
  - `VectorizationService`：全面采用语义切块替换硬性 Token 切分


## [0.5.0] - 2026-08-17

### 新增
- **智能体工作流与工具调用 (Agentic Workflow & Tool Calling)**：
  - `AccountTool`：用户 Token 配额与余额动态查询工具
  - `StudyPlannerTool`：自适应学习排期与提分目标计算工具
  - `KnowledgeSearchTool`：私有 pgvector 动态语义检索工具
  - `AgenticChatService`：多工具意图分发引擎与 LLM 融合生成，支持 Fallback 机制
  - `AgentController`：`/api/agent/chat` 智能体交互端点与多租户权限校验
  - 前端 API 拓展：新增 `agentChat` 服务端点封装与类型系统支持


### 新增
- **后端 RBAC 鉴权系统**：基于 Spring Boot 4.1 + Spring Security 的无状态 JWT 鉴权
  - `JwtTokenProvider`：JWT 签发、解析与验签
  - `JwtAuthenticationFilter`：无状态请求拦截过滤器
  - `SecurityConfig`：禁用 Session，启用方法级安全注解
  - `AuthController`：`/api/auth/login` 与 `/api/auth/register` 端点
  - `AdminController`：`@PreAuthorize("hasRole('ADMIN')")` 方法级拦截
  - `sys_user` 数据表与 Flyway 迁移脚本
  - 默认管理员账户初始化（admin/admin123）
- **前端登录与路由守护**：React 19 + TypeScript + Vite
  - 登录页面：暗黑模式自适应、高对比度无障碍设计
  - API 客户端：统一请求封装，自动携带 JWT Header
  - `AuthContext`：用户认证状态管理
  - `RequireAuth`：路由守卫组件，支持角色级拦截
- **测试覆盖**：
  - 后端：16 个 JUnit 测试，覆盖率 93.4%
  - 前端：9 个 Vitest 测试，覆盖率 84.59%

## [0.2.0] - 2026-06-12

### 新增
- **异步知识库管理中心**：基于 Spring AI 2.0.0-RC2 + S3 的语料 ETL 管道
  - `S3Service`：S3 预签名 URL 生成，前端直传对象存储
  - `VectorizationService`：`@Async` 异步文本切片与 pgvector 向量化
  - `KnowledgeController`：任务创建、状态查询 REST API
  - `knowledge_task` 状态机表（PENDING → PROCESSING → SUCCESS/FAILED）
  - 前端拖拽上传组件 `FileUpload` 与任务状态轮询列表 `TaskList`

## [0.3.0] - 2026-06-12

### 新增
- **Token 无锁并发计费中枢**：基于 Redis Lua 脚本的高并发防超卖计费引擎
  - `BillingService`：Lua 原子扣费脚本，余额查询-判断-扣减一步完成
  - `@Scheduled` 定时同步：Redis 账本批量持久化至 PostgreSQL
  - `BillingController`：余额查询、充值、扣费历史 REST API
  - `sys_user_quota` 与 `sys_billing_log` 数据表
  - 前端配额看板 `BillingPage`：余额展示、充值、扣费记录
  - `QuotaCard` 全局余额卡片，低余额预警提示

## [0.4.0] - 2026-06-12

### 新增
- **RAG 智能对话引擎**：基于 Spring AI 的多租户隔离检索增强对话
  - `RagService`：pgvector 向量检索 + 租户级数据隔离
  - `ChatController`：SSE 流式对话端点 `/api/chat/stream`
  - 流结束自动扣费：基于 Usage 元数据触发 Redis Lua 扣费
  - 前端 `ChatPage`：打字机效果对话界面、SSE 流式接收
  - 余额不足拦截：对话前检查余额，不足时提示充值

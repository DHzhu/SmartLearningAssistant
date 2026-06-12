# Product Vision & Features - Smart Learning Assistant

## Vision
基于 RAG（检索增强生成）架构 of SaaS 级多租户智能学习平台，具备企业级权限隔离、异步知识库构建与无锁高并发 Token 计费能力。

## Core Features
1. **RBAC 角色权限与安全接入 (RBAC & Secure Access)**
   - JWT 身份凭证：用户登录签发包含 `User_ID` 和 `Role` (`ROLE_USER` / `ROLE_ADMIN`) 的 Token。
   - 方法级隔离：所有管理员专有接口强制使用 `@PreAuthorize("hasRole('ADMIN')")` 和 SpEL 表达式进行精确拦截。

2. **异步知识库管理中心 (Asynchronous Knowledge Base Management)**
   - S3 物理落盘：管理员上传原始资料，后端直传 S3 获取 URL，不将大文件直接写入数据库。
   - 异步向量化 (`@Async`)：后台独立线程接管耗时任务，调用 `TokenTextSplitter` 切分文本，并由 Gemini 转化为向量后写入 pgvector。
   - 状态机闭环：维护 `knowledge_task` 表，前端轮询获取 `PENDING` -> `SUCCESS`/`FAILED` 的解析状态。

3. **Token 无锁并发计费中枢 (Token Billing Engine)**
   - 防超卖引擎 (Lua Script)：将“查余额 -> 判断 -> 扣减”逻辑封装为原子 Lua 脚本交由 Redis 执行，避免并发超卖。
   - 异步持久化：通过 `@Scheduled` 定时任务，将 Redis 内存账本定期同步回 PostgreSQL 的 `sys_user_quota` 表。

4. **RAG 智能对话引擎 (RAG Chat Engine)**
   - 租户级检索：RAG 查询 SQL 强制附加 `WHERE user_id = ?`，实现物理级数据隔离。
   - 混合检索引擎：结合 pgvector 相似度计算与 PG 内置的 BM25 关键词检索，提升精准度。
   - 流式拦截与扣费：`ChatClient.stream()` 通过 SSE 输出结果，并在流结束回调中提取大模型返回 of `Usage` 元数据，触发 Lua 脚本执行扣费。

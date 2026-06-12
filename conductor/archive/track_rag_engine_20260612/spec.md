# Track Specification: RAG 智能对话引擎

## 1. Goal
实现多租户隔离的 RAG（检索增强生成）智能对话引擎。结合 pgvector 相似度与 PostgreSQL 的 BM25 关键词进行混合检索，编排 Context 提供给 Gemini。通过 SSE（Server-Sent Events）向前端流式吐出回复，并在流结束的回调中根据大模型返回的 Usage 元数据进行扣费结算。

## 2. User Stories
- **As a User**: 我希望在一个美观的打字机对话框中与我的知识库进行对话，当输入问题时，AI 能即时流式吐出答案，并且展示引用的参考文档来源。
- **As an Admin**: 我需要系统保证用户提问时，AI 检索的背景知识语料绝不超出该用户上传的物理范围，即多租户数据完全物理隔离。

## 3. Technical Strategy
- **Core Approach**:
  - **混合检索与租户隔离**:
    - 获取用户请求后，后端首先从 Security Context 获取租户身份 `user_id`。
    - 组合 RAG 检索：首先通过 pgvector 计算向量余弦距离检索最相似的文档块，同时结合 PostgreSQL 特征词全文检索。
    - 限制 SQL / pgvector 查询逻辑必须附加 `WHERE user_id = ?`，实现物理租户层的数据硬隔离。
  - **大模型流式调用 & SSE 响应**:
    - 使用 Spring AI 的 `ChatClient.stream()` 调用 Gemini 接口。
    - 后端使用 `SseEmitter` 封装流式响应，向 React 前端持续推送数据。
  - **流结束结算**:
    - 订阅流终止回调，提取 Gemini API 随流末端返回的 `Usage` 元数据（包括 prompt tokens 和 generation tokens）。
    - 触发 Redis Lua 扣费脚本进行高并发扣费，生成计费流水。
  - **前端打字机效果**:
    - 在 React 中开发支持 Markdown 渲染和代码高亮的对话流气泡，实现渐进打字机动画。

## 4. Constraints & Standards
- 对话检索的 Context 块数量与长度要合理控制（如 topK=3 文本块），避免超出 Gemini 上下文窗口或引起高昂扣费。
- SSE 连接必须在发生异常、用户主动取消或超时的情况下彻底销毁 `SseEmitter`，释放后端虚拟线程资源。
- 扣费脚本的调用必须属于流式处理管道的最后一步，不能阻塞聊天输出的流畅度。

## 5. Success Criteria
- [ ] 混合检索逻辑能正确合并 pgvector 和文本关键词结果，且强制过滤 `user_id`。
- [ ] 前端对话框能以流式（打字机效果）展示数据，不出现严重的延迟或卡顿。
- [ ] 流断开或自然结束时，后端控制台能打印 Usage，且 Redis 中的用户配额被成功扣减。
- [ ] Java 与 TypeScript 的单元测试覆盖率均大于 80%。

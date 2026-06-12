# Track Specification: 异步知识库管理中心

## 1. Goal
构建安全高效的知识语料 ETL（抽取、转换、加载）管道，实现前端语料上传，物理文件落盘 S3 兼容存储，后端多线程异步文档解析切分，通过 Gemini 向量化后持久化写入 PostgreSQL pgvector 向量库。

## 2. User Stories
- **As an Admin**: 我希望在后台管理界面上传 PDF/Word 等多种学习语料，并能够实时轮询看到文件的解析和向量化处理进度（如解析中、解析成功、解析失败）。
- **As a Developer**: 我希望上传大文件时，文件直传到 S3 存储，由数据库只保存引用 URL，以保证高并发下的服务稳定性。

## 3. Technical Strategy
- **Core Approach**:
  - **物理存储与元数据**:
    - 前端请求后端获取 S3 预签名 URL（Presigned URL），文件直传 S3 兼容对象存储。
    - 后端在 PostgreSQL 中建立 `knowledge_task` 状态机表，记录语料的基本属性、S3 URL 及处理状态。
  - **异步向量化管道 (`@Async`)**:
    - 在 Spring Boot 中配置独立的线程池，由标注了 `@Async` 的服务方法接管解析任务。
    - 调用 Spring AI 的 `TokenTextSplitter` 对文本进行智能分块（Chunking）。
    - 调用 Google Gemini Embedding API 将文本块转换为 768 维的向量。
    - 将向量与元数据写入 pgvector，自动触发 HNSW 索引重构以加速检索。
  - **前端状态闭环**:
    - 使用 React 封装带拖拽功能的上传组件。
    - 设置定时轮询接口，实时查询 `knowledge_task` 中的处理状态，并在前端列表中以不同颜色指示器展示。

## 4. Constraints & Standards
- S3 的访问密钥与凭证必须使用环境变量配置，不能硬编码在代码中。
- 大于 10MB 的文档切片向量化需要进行限流或合理并发控制，以防超出 Gemini API 限制。
- 每一个向量记录必须附加 `user_id` 元数据，从而在检索时能够强过滤实现租户隔离。

## 5. Success Criteria
- [ ] 语料直传 S3 成功，且数据库成功插入任务记录。
- [ ] 异步任务能成功触发文本解析、切分，向量成功存入 PostgreSQL 且维度符合 768 维。
- [ ] 发生异常（如大模型限流、网络超时）时，状态机能正确记录并修改为 `FAILED`，且记录错误日志。
- [ ] Java 与 TypeScript 的单元测试覆盖率均大于 80%。

# Implementation Plan: 异步知识库管理中心

## 1. Research & Analysis
- [x] 调研 Spring AI 文本切片器 `TokenTextSplitter` 性能及 Gemini 向量模型的维度匹配问题。 [SHA: 17917d2]
- [x] 调研 S3 前端预签名直传（Presigned URL）安全机制。 [SHA: 17917d2]
- [x] 最终确认 `conductor/tracks/track_knowledge_base/spec.md` 技术方案。 [SHA: 17917d2]

## 2. Preparation & Foundation
- [x] 后端引入 AWS SDK S3 客户端依赖，配置 S3 与 PostgreSQL 数据库连接。 [SHA: 17917d2]
- [x] 后端创建 `knowledge_task` 与 `knowledge_vector`（配合 pgvector 的 Spring AI 模型）数据库表。 [SHA: 17917d2]

## 3. Implementation
- [x] **后端**：编写 S3 预签名 URL 获取服务与文件状态管理控制器。 [SHA: 17917d2]
- [x] **后端**：基于 `@Async` 和自定义线程池实现异步文档切片与向量化存储流水线。 [SHA: 17917d2]
- [x] **前端**：编写文件上传与拖拽 React 组件，实现基于预签名 URL 的直传逻辑。 [SHA: 17917d2]
- [x] **前端**：实现语料任务状态列表，进行定时器轮询拉取解析进度。 [SHA: 17917d2]

## 4. Verification & Hardening
- [x] 编写后端测试模拟大文件切片及 pgvector 写入，验证相似度计算检索。 [SHA: 17917d2]
- [x] 编写前端测试，测试文件上传各状态下的 UI 响应。 [SHA: 17917d2]
- [x] 校验 Checkstyle 与 ESLint 等静态分析规范。 [SHA: 17917d2]

## 5. Track Closure & Archiving
- [x] 按照 `workflow.md` 执行完备归档协议：
    - [x] 分别在 `backend` 目录下执行 `./mvnw clean package -DskipTests` 和 `frontend` 目录下执行 `npm run build` 确保整体验收无误。 [SHA: 17917d2]
    - [x] 在 `CHANGELOG.md` 中以中文详细记录此特性的变更内容。 [SHA: 17917d2]
    - [x] 将当前轨道的 `metadata.json` 状态修改为 "done"，并追加日期后缀重命名轨道 ID。 [SHA: 17917d2]
    - [x] 将轨道文件夹移动至 `conductor/archive/`。 [SHA: 17917d2]
    - [x] 在 `conductor/tracks.md` 中将当前轨道条目移入归档区。 [SHA: 17917d2]
    - [x] 执行最终的归档 Git 提交。 [SHA: 17917d2]

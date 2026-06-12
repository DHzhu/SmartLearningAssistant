# Implementation Plan: RAG 智能对话引擎

## 1. Research & Analysis
- [ ] 调研 Spring AI 混合检索框架和自定义 `DocumentRetriever` 的扩展。 [SHA: ]
- [ ] 调研 SSE（Server-Sent Events）长连接断开检测与 Spring AI Usage 提取。 [SHA: ]
- [ ] 最终确认 `conductor/tracks/track_rag_engine/spec.md` 技术方案。 [SHA: ]

## 2. Preparation & Foundation
- [ ] 验证 pgvector 插件和 PostgreSQL 全文检索（BM25）能在本机的 PG17 正常联动工作。 [SHA: ]
- [ ] 在 `backend` 中初始化 Spring AI Gemini ChatClient 配置。 [SHA: ]

## 3. Implementation
- [ ] **后端**：编写租户级 `TenantVectorStore` 和混合检索引擎。 [SHA: ]
- [ ] **后端**：实现 SSE 对话流控制器 `/api/chat/stream`，捕获流终止事件提取 Usage 计费。 [SHA: ]
- [ ] **前端**：编写 React 对话组件，包含输入框、消息气泡流及 Markdown 解析渲染。 [SHA: ]
- [ ] **前端**：通过 Fetch / EventSource 读取 SSE 流式数据并呈现平滑打字机动画效果。 [SHA: ]

## 4. Verification & Hardening
- [ ] 编写测试案例验证混合检索召回的准确性及多租户交叉访问阻断（403 或无结果）。 [SHA: ]
- [ ] 编写流式输出压测，验证高并发 SSE 连接下系统的内存占用和线程释放。 [SHA: ]
- [ ] 校验 Checkstyle 与 ESLint 等静态分析规范。 [SHA: ]

## 5. Track Closure & Archiving
- [ ] 按照 `workflow.md` 执行完备归档协议：
    - [ ] 分别在 `backend` 目录下执行 `./mvnw clean package -DskipTests` 和 `frontend` 目录下执行 `npm run build` 确保整体验收无误。 [SHA: ]
    - [ ] 在 `CHANGELOG.md` 中以中文详细记录此特性的变更内容。 [SHA: ]
    - [ ] 将当前轨道的 `metadata.json` 状态修改为 "done"，并追加日期后缀重命名轨道 ID。 [SHA: ]
    - [ ] 将轨道文件夹移动至 `conductor/archive/`。 [SHA: ]
    - [ ] 在 `conductor/tracks.md` 中将当前轨道条目移入归档区。 [SHA: ]
    - [ ] 执行最终的归档 Git 提交。 [SHA: ]

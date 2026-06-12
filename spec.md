# 📚 智能学习助手 (Smart Learning Assistant) - 架构与技术详细说明书 (SPEC)

**文档版本:** v4.0 (终极实战落地版)  
**项目状态:** 基础设施 (PG 17, Redis, S3) 已部署完毕，进入应用层研发阶段。  
**项目定位:** 基于 RAG（检索增强生成）架构的 SaaS 级多租户智能学习平台，具备企业级权限隔离、异步知识库构建与无锁高并发 Token 计费能力。

---

## 1. 核心技术栈与版本基线 (Tech Stack & Versions)

本项目包含 Java 后端与 React 前端，采用前后端分离架构，核心技术基准如下：

### 后端技术栈 (Backend Tech Stack)
| 组件类别 | 技术选型 | 锁定版本 | 核心职责说明 |
| :--- | :--- | :--- | :--- |
| **基础框架** | **Spring Boot** | **`4.x`** | 提供底层 IoC 容器，全面拥抱 Java 21 与虚拟线程 (Loom)。 |
| **AI 编排** | **Spring AI** | **`2.0.x (主分支)`** | 统一大模型调用规范，提供 `ChatClient` 流式调用与 RAG 组件支持。 |
| **安全网关** | **Spring Cloud Gateway** | 与 Spring Boot 匹配 | 统一流量入口，负责基于 Redis 的全局 API 令牌桶防刷限流。 |
| **鉴权框架** | **Spring Security** | 与 Spring Boot 匹配 | 结合 JWT 实现无状态鉴权与 `@PreAuthorize` 细粒度角色拦截。 |
| **核心数据库** | **PostgreSQL** | `17` *(已部署)* | 统一存储关系型数据（用户、配额、文档元数据），配合插件实现高阶检索。 |
| **向量插件** | **pgvector** | `pg17` *(已部署)* | 赋能 PG 向量存储与语义检索能力。 |
| **缓存与计费** | **Redis** | `7.x` *(已部署)* | 存储对话记忆 (`ChatMemory`)、执行 Lua 脚本实现无锁高并发计费。 |
| **对象存储** | **S3 兼容存储** | *(已部署)* | 物理存储用户上传的 PDF、Word 等原始语料。 |
| **大模型引擎** | **Google Gemini** | Gemini Pro / Flash | 提供 Embedding 向量化算力与 Chat 对话推理算力。 |

### 前端技术栈 (Frontend Tech Stack)
| 组件类别 | 技术选型 | 锁定版本 | 核心职责说明 |
| :--- | :--- | :--- | :--- |
| **核心框架** | **React** | **`18.x` / `19.x`** | 提供声明式 UI 渲染，构建现代单页应用 (SPA)。 |
| **构建工具** | **Vite** | **`5.x`** | 提供极速的本地开发热重载与生产打包构建。 |
| **开发语言** | **TypeScript** | **`5.x`** | 强类型类型校验，提升业务代码健壮性与可读性。 |
| **样式方案** | **Vanilla CSS** | - | 高灵活性、精细化 UI 控制，原生支持 CSS 变量实现暗黑模式切换。 |
| **网络请求** | **Fetch API / Axios** | - | 实现与后端的异步 RESTful API 通信及 SSE 流式对话接收。 |

---

## 2. 核心模块与业务骨架 (Core Modules)

### 🛡️ 模块一：RBAC 角色权限与安全接入
* **JWT 身份凭证：** 用户登录签发包含 `User_ID` 和 `Role` (`ROLE_USER` / `ROLE_ADMIN`) 的 Token。
* **方法级隔离：** 所有管理员专有接口强制使用 `@PreAuthorize("hasRole('ADMIN')")` 和 SpEL 表达式进行精确拦截。

### 📂 模块二：异步知识库管理中心 (ETL 数据管道)
* **S3 物理落盘：** 管理员上传原始资料，后端直传 S3 获取 URL，绝不将大文件塞入数据库。
* **异步向量化 (`@Async`)：** 后台独立线程接管耗时任务，调用 `TokenTextSplitter` 切分文本，并由 Gemini 转化为向量后写入 pgvector。
* **状态机闭环：** 维护 `knowledge_task` 表，前端轮询获取 `PENDING` -> `SUCCESS`/`FAILED` 的解析状态。

### 💰 模块三：Token 无锁并发计费中枢
* **防超卖引擎 (Lua Script)：** 针对 AI 对话高频扣费痛点，将“查余额 -> 判断 -> 扣减”逻辑封装为原子 Lua 脚本交由 Redis 执行，彻底消灭并发超卖。
* **异步持久化：** 通过 `@Scheduled` 定时任务，将 Redis 内存账本定期 Sync 回 PostgreSQL 的 `sys_user_quota` 表。

### 🤖 模块四：RAG 智能对话引擎
* **租户级检索：** RAG 查询 SQL 强制附加 `WHERE user_id = ?`，实现物理级数据隔离。
* **混合检索引擎：** 结合 pgvector 相似度计算与 PG 内置的 BM25 关键词检索，提升精准度。
* **流式拦截与扣费：** `ChatClient.stream()` 通过 SSE 输出结果，并在流终止回调中提取大模型返回的 `Usage` 元数据，触发 Lua 脚本执行扣费。

## 3. 项目目录结构与初始化配置 (Project Structure & Bootstrap)

### 3.1 目录结构 (Directory Layout)

本项目采用前后端分离的 Monorepo 目录布局，结构定义如下：

```text
/projects/SmartLearningAssistant/
├── backend/                       # Java 后端模块 (Spring Boot)
│   ├── src/                       # 后端源码目录
│   │   ├── main/
│   │   │   ├── java/              # Java 代码
│   │   │   └── resources/         # 配置文件、Lua 脚本、SQL 迁移
│   │   └── test/                  # 后端单元与集成测试
│   ├── pom.xml                    # Maven 项目依赖管理
│   └── mvnw                       # Maven Wrapper 脚本
├── frontend/                      # React 前端模块 (Vite + TypeScript)
│   ├── src/                       # 前端源码目录
│   │   ├── assets/                # 静态资源 (图片、字体)
│   │   ├── components/            # 通用 UI 组件
│   │   ├── hooks/                 # 自定义 React Hooks
│   │   ├── services/              # API 服务层 (Fetch / SSE 处理)
│   │   ├── styles/                # CSS 样式文件 (Vanilla CSS)
│   │   ├── App.tsx                # 应用入口组件
│   │   └── main.tsx               # DOM 挂载入口
│   ├── package.json               # npm 依赖与脚本配置
│   ├── vite.config.ts             # Vite 构建与开发代理配置
│   ├── tsconfig.json              # TypeScript 编译器配置
│   └── index.html                 # 单页 HTML 模板
├── conductor/                     # Conductor 任务编排与设计管理
│   ├── tracks/                    # 活跃开发 Track 状态与计划
│   ├── templates/                 # 新增 Track 物理模板 (metadata/spec/plan)
│   ├── code_styleguides/          # 编码规范指南
│   ├── tech-stack.md              # 统一技术栈清单
│   └── tracks.md                  # 主路线图与 Track 列表
├── .agent/
│   └── AGENT.md                   # Agent 主规范协议
├── spec.md                        # 系统架构设计规格说明书 (本文件)
├── GEMINI.md                      # Gemini 协调入口
├── CLAUDE.md                      # Claude 协调入口
└── CHANGELOG.md                   # 变更历史日志
```

### 3.2 后端核心构建依赖 (backend/pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-gemini-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.25.x</version>
    </dependency>
</dependencies>
```

### 3.3 后端核心配置 (backend/src/main/resources/application.yml)
```yaml
spring:
  application:
    name: smart-learning-assistant

  # 1. Redis 连接 (计费账本与记忆缓存)
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}

  # 2. PostgreSQL 17 连接 (关系型与向量数据)
  datasource:
    url: jdbc:postgresql://${PG_HOST:localhost}:5432/learning_bot
    username: ${PG_USER}
    password: ${PG_PASSWORD}

  ai:
    # 3. Gemini 大模型配置
    gemini:
      api-key: ${GEMINI_API_KEY}
      chat:
        options:
          model: gemini-1.5-pro 
    
    # 4. pgvector 向量库配置
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 768 # 须与 Embedding 模型输出维度严格一致
        initialize-schema: true # 极客特性：Spring AI 启动时自动建立向量表结构

# 5. S3 兼容存储配置
s3:
  endpoint: ${S3_ENDPOINT}
  access-key: ${S3_ACCESS_KEY}
  secret-key: ${S3_SECRET_KEY}
  region: ${region}
  bucket-name: ${bucket_name}
```

### 3.4 前端构建依赖与脚本 (frontend/package.json)
```json
{
  "name": "smart-learning-assistant-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "preview": "vite preview",
    "test": "vitest run",
    "coverage": "vitest run --coverage"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^6.28.0"
  },
  "devDependencies": {
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@typescript-eslint/eslint-plugin": "^8.0.0",
    "@typescript-eslint/parser": "^8.0.0",
    "@vitejs/plugin-react": "^4.3.0",
    "eslint": "^9.0.0",
    "typescript": "^5.5.0",
    "vite": "^5.4.0",
    "vitest": "^2.0.0"
  }
}
```

### 3.5 前端开发配置 (frontend/vite.config.ts)
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/api/chat/stream': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      }
    }
  }
});
```

---

## 4. 关键数据流路径 (Data Flows)

1.  **用户提问：** 前端 (JWT) -> 网关 (防刷) -> Spring Security (鉴权) -> Redis (余额预检)。
2.  **RAG 召回：** PostgreSQL (私有知识混合检索) -> Spring AI 编排提示词 -> Gemini API。
3.  **返回与结算：** Gemini 流式返回 -> SSE 推送前端 -> 流结束触发 `Usage` 提取 -> Redis Lua 原子扣费。

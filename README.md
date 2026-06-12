# 智能学习助手 (Smart Learning Assistant)

基于 RAG（检索增强生成）架构的 SaaS 级多租户智能学习平台，具备企业级权限隔离、异步知识库构建与无锁高并发 Token 计费能力。

## 核心功能

### 🛡️ RBAC 角色权限与安全接入
- JWT 无状态身份认证，支持 `ROLE_USER` / `ROLE_ADMIN` 角色
- Spring Security 方法级拦截 (`@PreAuthorize`)
- 前端路由守卫，未授权自动重定向

### 📂 异步知识库管理中心
- 管理员上传 PDF/Word/TXT 学习语料
- S3 预签名 URL 直传，不经过后端中转
- `@Async` 异步文本切片 + Gemini 向量化 + pgvector 存储
- 前端拖拽上传 + 实时状态轮询

### 💰 Token 无锁并发计费
- Redis Lua 脚本原子扣费，彻底消灭并发超卖
- `@Scheduled` 定时同步 Redis 账本至 PostgreSQL
- 前端配额看板：余额展示、充值、扣费记录

### 🤖 RAG 智能对话引擎
- pgvector 向量检索 + 租户级数据物理隔离
- SSE 流式对话输出，前端打字机效果
- 流结束自动提取 Usage 元数据并触发扣费

## 技术栈

| 层级 | 技术选型 |
|------|---------|
| 后端框架 | Java 21, Spring Boot 4.1, Spring Security, Spring AI 2.0.0-RC2 |
| 数据库 | PostgreSQL 17 + pgvector, Redis 7.x |
| 对象存储 | S3 兼容存储 (MinIO / AWS S3) |
| 大模型 | Google Gemini (Embedding + Chat) |
| 前端框架 | React 19, TypeScript 5.x, Vite 5.x |
| 测试 | JUnit 5, Vitest, JaCoCo (>80% 覆盖率) |

## 项目结构

```
SmartLearningAssistant/
├── backend/                          # Java 后端
│   ├── src/main/java/.../
│   │   ├── auth/                     # JWT 鉴权
│   │   ├── admin/                    # 管理员接口
│   │   ├── billing/                  # Token 计费
│   │   ├── chat/                     # RAG 对话引擎
│   │   ├── knowledge/                # 知识库管理
│   │   ├── user/                     # 用户实体
│   │   ├── config/                   # 配置类
│   │   └── common/                   # 公共组件
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置
│   │   ├── db/migration/             # Flyway 迁移脚本
│   │   └── lua/                      # Redis Lua 脚本
│   └── pom.xml
├── frontend/                         # React 前端
│   ├── src/
│   │   ├── components/
│   │   │   ├── Login/                # 登录页面
│   │   │   ├── Chat/                 # 对话界面
│   │   │   ├── Knowledge/            # 知识库管理
│   │   │   ├── Billing/              # 配额看板
│   │   │   └── Common/               # 通用组件
│   │   ├── services/                 # API 服务层
│   │   ├── context/                  # React Context
│   │   └── types/                    # TypeScript 类型
│   └── package.json
├── conductor/                        # 任务编排管理
│   ├── tracks/                       # 活跃轨道
│   └── archive/                      # 已完成轨道
├── spec.md                           # 架构设计规格说明书
└── CHANGELOG.md                      # 变更日志
```

## 快速开始

### 环境要求

- Java 21+
- Node.js 22+
- Maven 3.9+
- PostgreSQL 17 (已启用 pgvector 扩展)
- Redis 7.x
- S3 兼容存储 (如 MinIO)

### 1. 克隆项目

```bash
git clone git@github.com:DHzhu/SmartLearningAssistant.git
cd SmartLearningAssistant
```

### 2. 配置环境变量

复制并编辑后端配置：

```bash
cd backend
cp src/main/resources/application.yml src/main/resources/application-local.yml
```

编辑 `application-local.yml`，填入实际的连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/learning_bot
    username: your_pg_user
    password: your_pg_password

  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

  ai:
    google:
      genai:
        api-key: your_gemini_api_key

jwt:
  secret: your-jwt-secret-key-at-least-256-bits-long

s3:
  endpoint: http://localhost:9000
  access-key: your_s3_access_key
  secret-key: your_s3_secret_key
  region: us-east-1
  bucket-name: learning-assistant
```

或使用环境变量：

```bash
export PG_HOST=localhost
export PG_USER=postgres
export PG_PASSWORD=your_password
export REDIS_HOST=localhost
export GEMINI_API_KEY=your_api_key
export JWT_SECRET=your-jwt-secret-key-at-least-256-bits-long
export S3_ENDPOINT=http://localhost:9000
export S3_ACCESS_KEY=your_access_key
export S3_SECRET_KEY=your_secret_key
export S3_BUCKET_NAME=learning-assistant
```

### 3. 初始化数据库

```sql
CREATE DATABASE learning_bot;
CREATE EXTENSION IF NOT EXISTS vector;  -- pgvector 扩展
```

Flyway 会在启动时自动创建表结构。

### 4. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，API 请求自动代理到后端。

### 6. 访问应用

- 打开浏览器访问 `http://localhost:5173`
- 默认管理员账号：`admin` / `admin123`
- 默认普通用户：`user` / `user123`

## API 端点

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录，返回 JWT |
| POST | `/api/auth/register` | 用户注册 |

### 知识库 (需要 ADMIN 角色)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/knowledge/upload-url` | 获取 S3 预签名上传 URL |
| POST | `/api/knowledge/tasks` | 创建向量化任务 |
| GET | `/api/knowledge/tasks` | 查询当前用户的任务列表 |
| GET | `/api/knowledge/tasks/{id}` | 查询指定任务状态 |

### 计费

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/billing/balance` | 查询 Token 余额 |
| POST | `/api/billing/recharge?amount=10000` | 充值 Token |
| GET | `/api/billing/history` | 查询扣费记录 |

### 对话

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/chat/stream` | SSE 流式对话 |
| POST | `/api/chat/sync` | 同步对话（非流式） |

## 运行测试

```bash
# 后端测试 + 覆盖率
cd backend
./mvnw test
./mvnw jacoco:report

# 前端测试 + 覆盖率
cd frontend
npm test
npm run coverage
```

## 部署

### Docker 部署 (推荐)

```dockerfile
# 后端 Dockerfile
FROM eclipse-temurin:21-jre
COPY backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 构建
cd backend && ./mvnw clean package -DskipTests
docker build -t smart-learning-backend .

# 运行
docker run -d \
  -p 8080:8080 \
  -e PG_HOST=your_pg_host \
  -e PG_USER=your_pg_user \
  -e PG_PASSWORD=your_pg_password \
  -e REDIS_HOST=your_redis_host \
  -e GEMINI_API_KEY=your_api_key \
  -e JWT_SECRET=your_jwt_secret \
  -e S3_ENDPOINT=your_s3_endpoint \
  -e S3_ACCESS_KEY=your_access_key \
  -e S3_SECRET_KEY=your_secret_key \
  -e S3_BUCKET_NAME=your_bucket \
  smart-learning-backend
```

### 前端静态部署

```bash
cd frontend
npm run build
# 将 dist/ 目录部署到 Nginx 或 CDN
```

Nginx 配置示例：

```nginx
server {
    listen 80;
    root /var/www/smart-learning;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/chat/stream {
        proxy_pass http://localhost:8080;
        proxy_set_header Connection '';
        proxy_http_version 1.1;
        chunked_transfer_encoding off;
        proxy_buffering off;
        proxy_cache off;
    }
}
```

## 许可证

MIT License

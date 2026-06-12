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

## [0.1.0] - 2026-06-11

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
  - 前端拖拽上传组件 `FileUpload` 与任务状态轮询列表 `TaskList

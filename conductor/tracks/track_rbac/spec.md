# Track Specification: RBAC 角色权限与安全接入

## 1. Goal
实现完整的多租户 RBAC 权限系统与安全准入机制，包括后端 JWT 鉴权与方法拦截、前端登录界面与路由守护（Route Guards）。

## 2. User Stories
- **As a User**: 我希望能够通过账号密码登录，并获取合法的 JWT Token，以便后续请求能够安全地调用我的专属学习助手 API。
- **As an Admin**: 我希望进入管理员专属页面上传原始语料，并且非管理员用户无法通过任何接口访问这些敏感管理接口。

## 3. Technical Strategy
- **Core Approach**:
  - **后端鉴权**：
    - 设计无状态的 JWT 生成与解析工具类（包含 `User_ID` 与 `Role` 载荷）。
    - 配置 Spring Security，禁用内置的 Session 会话与 FormLogin，加入自定义的 `JwtAuthenticationFilter` 对每个 HTTP 请求进行无状态拦截。
    - 使用方法级注解（如 `@PreAuthorize("hasRole('ADMIN')")`）和 SpEL 表达式，对管理端的接口进行严格的方法隔离拦截。
  - **前端鉴权**：
    - 使用 React 19 + Vanilla CSS 开发扁平化、现代感强的登录页面（适配暗黑模式）。
    - 登录成功后将 JWT 缓存在 SessionStorage 或前端 Context/Zustand 状态中。
    - 配置 React Router，实现带角色路由守护（RequireAuth Route Wrapper），未登录或无权用户自动重定向到 `/login`。

## 4. Constraints & Standards
- JWT Token 必须设置合理的过期时间，且不在网络中传输敏感的用户密码。
- 后端所有针对业务数据的操作（如对话、知识库检索）必须能从 Token 中解析出 `User_ID`，用于执行物理级多租户数据隔离。
- 前端登录界面必须符合高对比度及无障碍标准，并自适应暗黑模式。

## 5. Success Criteria
- [ ] 后端通过 JUnit 验证 JWT 的解析和安全过滤链。
- [ ] 测试方法级注解对非 ADMIN 请求返回 403 拦截。
- [ ] 前端测试登录表单的逻辑和路由守卫在未授权下的自动重定向。
- [ ] Java 与 TypeScript 的类型检查、代码规范全部通过，且测试覆盖率 >80%。

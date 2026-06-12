# Implementation Plan: RBAC 角色权限与安全接入

## 1. Research & Analysis
- [ ] 调研 Spring Boot 4.x / Spring Security 无状态过滤链配置。 [SHA: ]
- [ ] 调研 React 19 / React Router 6.x/7.x 的路由守卫最佳实践。 [SHA: ]
- [ ] 在 `conductor/tracks/track_rbac/spec.md` 中最终确认安全设计规范。 [SHA: ]

## 2. Preparation & Foundation
- [ ] 在项目根目录下创建 `backend` 与 `frontend` 独立目录，初始化各自的依赖管理（`backend/pom.xml`, `frontend/package.json`）。 [SHA: ]
- [ ] 配置好后端和前端的本地开发验证环境，引入 JUnit 与 Vitest。 [SHA: ]

## 3. Implementation
- [ ] **后端**：编写 `JwtTokenProvider` 负责 JWT 签发、解密与验签逻辑。 [SHA: ]
- [ ] **后端**：实现 `JwtAuthenticationFilter` 与 Spring Security 配置类，挂载无状态过滤链。 [SHA: ]
- [ ] **后端**：建立 `sys_user` 数据库基本实体，编写登录控制器 `/api/auth/login` 与方法拦截测试控制器 `/api/admin/system`。 [SHA: ]
- [ ] **前端**：编写 React 登录页面与自适应 CSS 样式。 [SHA: ]
- [ ] **前端**：封装 API 客户端及 SSE 数据通道基础拦截器（支持在 Header 中携带 JWT）。 [SHA: ]
- [ ] **前端**：在 React Router 中实现 `<RequireAuth>` 路由拦截组件。 [SHA: ]

## 4. Verification & Hardening
- [ ] 编写后端的集成测试，测试匿名请求、普通用户请求与管理员请求在鉴权拦截下的表现，并确保覆盖率 >80%。 [SHA: ]
- [ ] 编写前端的单元测试，模拟登录流程及路由拦截，并确保覆盖率 >80%。 [SHA: ]
- [ ] 修复可能存在的静态扫描警告（Checkstyle 与 ESLint）。 [SHA: ]

## 5. Track Closure & Archiving
- [ ] 按照 `workflow.md` 执行完备归档协议：
    - [ ] 分别在 `backend` 目录下执行 `./mvnw clean package -DskipTests` 和 `frontend` 目录下执行 `npm run build` 确保整体验收无误。 [SHA: ]
    - [ ] 在 `CHANGELOG.md` 中以中文详细记录此特性的变更内容。 [SHA: ]
    - [ ] 将当前轨道的 `metadata.json` 状态修改为 "done"，并追加日期后缀重命名轨道 ID。 [SHA: ]
    - [ ] 将轨道文件夹移动至 `conductor/archive/`。 [SHA: ]
    - [ ] 在 `conductor/tracks.md` 中将当前轨道条目移入归档区。 [SHA: ]
    - [ ] 执行最终的归档 Git 提交。 [SHA: ]

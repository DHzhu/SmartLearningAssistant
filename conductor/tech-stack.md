# Tech Stack - Smart Learning Assistant

## Core Technologies
### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.x, Spring AI 2.0.x
- **Build Tool**: Maven (pom.xml)

### Frontend
- **Language**: TypeScript 5.x, JavaScript (ES6+)
- **Framework**: React 18.x / 19.x (SPA)
- **Build Tool**: Vite 5.x, npm

## Core Libraries & Infrastructure
- **Database**: PostgreSQL 17 + pgvector (pg17)
- **Cache & Concurrency**: Redis 7.x (Lua scripting for rate limiting & token billing)
- **Object Storage**: S3 compatible storage (AWS SDK S3 client)
- **Security**: Spring Security + JWT + Spring Cloud Gateway
- **AI Integration**: Spring AI Gemini Starter
- **Frontend State & Router**: React Router, Context API / Zustand
- **Frontend Style**: Vanilla CSS

## Quality & Testing
### Backend Quality
- **Test Framework**: JUnit 5, Spring Boot Test
- **Mocking**: Mockito
- **Static Analysis**: Maven Checkstyle Plugin
- **Coverage Target**: >80% (JaCoCo)

### Frontend Quality
- **Test Framework**: Vitest / React Testing Library
- **Static Analysis**: ESLint, Prettier, TypeScript (`tsc --noEmit`)
- **Coverage Target**: >80%

## Verification Commands
### Backend Commands
- **Build**: `./mvnw clean package -DskipTests`
- **Test**: `./mvnw test`
- **Coverage**: `./mvnw jacoco:report`
- **Check**: `./mvnw compile`

### Frontend Commands (Run in frontend directory)
- **Build**: `npm run build`
- **Test**: `npm run test`
- **Coverage**: `npm run coverage`
- **Check**: `npm run lint` && `npx tsc --noEmit`


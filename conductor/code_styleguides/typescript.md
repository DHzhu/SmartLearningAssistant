# TypeScript & React Code Style Guide - Smart Learning Assistant

## 1. General Rules
- Follow official TypeScript guidelines and Airbnb JavaScript Style Guide.
- Use React 18/19 functional components with hooks.
- Use Vanilla CSS for all components. Keep styling modularized (e.g. CSS Modules or component-scoped styles).
- Strictly avoid `any` type. Define explicit types or interfaces for all props, states, and API responses.

## 2. Naming Conventions
- **React Components**: PascalCase (e.g., `ChatWindow.tsx`, `KnowledgeManager.tsx`)
- **Hooks**: camelCase prefixed with `use` (e.g., `useAuth.ts`, `useChatStream.ts`)
- **TypeScript Interfaces/Types**: PascalCase (e.g., `Message`, `UserQuota`)
- **CSS Class Names**: kebab-case (e.g., `.chat-bubble`, `.user-avatar-dark`)

## 3. Language Features & React Best Practices
- **Null Safety**: Use optional chaining `?.` and nullish coalescing `??`.
- **State Management**: Prefer local component state (`useState`) or Context API for lightweight sharing. Use a store (like Zustand) if global state complexity grows.
- **Asynchronous Flow**: Use `async/await` for Fetch API/Axios calls.
- **Resource Cleanup**: Always clean up event listeners, timers, and SSE (Server-Sent Events) event sources in the cleanup function of `useEffect`.

## 4. Frontend-Backend Integration
- **SSE Stream Processing**: Maintain robust error handling and reconnection strategies for `EventSource` when streaming chat completions from the Gemini model.
- **API Client**: Consolidate all RESTful API calls under an API service layer with uniform error handling.
- **JWT Storage**: Store authentication tokens securely (e.g., in memory or HttpOnly cookies where possible, or secure storage with clear state management).

## 5. Testing Specifics
- **Framework**: Vitest and React Testing Library.
- **Mocking**: Mock API responses using MSW (Mock Service Worker) or simple Vitest mocks.
- **Component Tests**: Prioritize testing user interactions (clicks, inputs) and rendering logic.

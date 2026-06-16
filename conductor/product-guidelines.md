# Product Guidelines - Smart Learning Assistant

## Prose Style
- **Tone**: Concise, professional, and practical.
- **Language**: English for code elements, multi-language for help/UI messages.

## User Experience (UX)
- **Zero Configuration**: Ready to use upon installation/initialization.
- **Context Awareness**: Leverage current environment/IDE context.
- **Error Gracefully**: Provide actionable error messages.

## Visual Identity
- Follow platform-native design systems (e.g., IntelliJ, Web-native).
- **Dark Mode Support**: Mandatory.
- **Frontend Design**: If frontend development or UI/UX design is involved, prioritize activating and using the `frontend-design` skill to ensure premium aesthetics and avoid generic defaults.

## Functional Constraints
- **Performance**: Operations must not block the main/UI thread.
- **State Persistence**: User data must be durable across restarts.

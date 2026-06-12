# General Code Style Principles

This document outlines general coding principles that apply across all languages and frameworks in this project.

## 1. Readability
- Code should be easy to read and understand by humans.
- Avoid overly clever or obscure constructs.

## 2. Consistency
- Follow existing patterns in the codebase.
- Maintain consistent formatting, naming, and structure.

## 3. Simplicity & Minimalist Design
- **Minimum Code**: Write the minimum code that solves the problem. Nothing speculative.
- **No Over-Engineering**: No abstractions for single-use code. No "flexibility" that wasn't requested.
- **Efficiency**: If it could be 50 lines, don't write 200.

## 4. Surgical Changes
- **Targeted Edits**: Touch only what you must. Match existing style, even if you disagree.
- **Controlled Cleanup**: Remove imports/variables/functions that YOUR changes made unused. Do not remove pre-existing dead code unless asked.
- **Traceability**: Every changed line should trace directly to the user's request.

## 5. Documentation
- Document **why** something is done, not just **what**.
- Keep documentation up-to-date with code changes.

package com.smartlearning.assistant.auth;

public record LoginResponse(
        String token,
        Long userId,
        String username,
        String role
) {
}

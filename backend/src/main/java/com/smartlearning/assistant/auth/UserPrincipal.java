package com.smartlearning.assistant.auth;

public record UserPrincipal(Long userId, String username, String role) {
}

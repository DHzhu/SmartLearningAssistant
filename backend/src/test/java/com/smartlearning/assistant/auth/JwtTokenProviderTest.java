package com.smartlearning.assistant.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-key-for-unit-testing-only-must-be-at-least-256-bits-long",
                3600000);
    }

    @Test
    void shouldGenerateAndParseToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser", "ROLE_USER");

        assertThat(token).isNotBlank();

        Claims claims = jwtTokenProvider.parseToken(token);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser", "ROLE_USER");

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                "test-secret-key-for-unit-testing-only-must-be-at-least-256-bits-long",
                0);

        String token = expiredProvider.generateToken(1L, "testuser", "ROLE_USER");

        // Token is generated with 0ms expiration, so it's already expired
        assertThat(expiredProvider.validateToken(token)).isFalse();
    }

    @Test
    void shouldExtractUserId() {
        String token = jwtTokenProvider.generateToken(42L, "admin", "ROLE_ADMIN");

        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(42L);
    }

    @Test
    void shouldExtractRole() {
        String token = jwtTokenProvider.generateToken(1L, "admin", "ROLE_ADMIN");

        assertThat(jwtTokenProvider.getRoleFromToken(token)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtTokenProvider.generateToken(1L, "alice", "ROLE_USER");

        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("alice");
    }
}

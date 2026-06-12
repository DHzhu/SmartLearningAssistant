package com.smartlearning.assistant.auth;

import com.smartlearning.assistant.user.SysUser;
import com.smartlearning.assistant.user.SysUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            SysUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String token = jwtTokenProvider.generateToken(
                            user.getId(), user.getUsername(), user.getRole());
                    return ResponseEntity.ok(new LoginResponse(
                            token, user.getId(), user.getUsername(), user.getRole()));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody LoginRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(409).build();
        }

        SysUser user = new SysUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                "ROLE_USER");

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(
                user.getId(), user.getUsername(), user.getRole());

        return ResponseEntity.ok(new LoginResponse(
                token, user.getId(), user.getUsername(), user.getRole()));
    }
}

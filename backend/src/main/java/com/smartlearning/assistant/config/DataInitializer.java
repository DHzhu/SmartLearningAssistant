package com.smartlearning.assistant.config;

import com.smartlearning.assistant.user.SysUser;
import com.smartlearning.assistant.user.SysUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            SysUser admin = new SysUser(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN");
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            SysUser user = new SysUser(
                    "user",
                    passwordEncoder.encode("user123"),
                    "ROLE_USER");
            userRepository.save(user);
        }
    }
}

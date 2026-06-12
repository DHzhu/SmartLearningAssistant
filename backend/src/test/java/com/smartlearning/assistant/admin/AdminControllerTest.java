package com.smartlearning.assistant.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smartlearning.assistant.auth.JwtTokenProvider;
import com.smartlearning.assistant.user.SysUser;
import com.smartlearning.assistant.user.SysUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class AdminControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        userRepository.deleteAll();

        SysUser admin = userRepository.save(new SysUser(
                "admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN"));
        SysUser user = userRepository.save(new SysUser(
                "normaluser", passwordEncoder.encode("user123"), "ROLE_USER"));

        adminToken = jwtTokenProvider.generateToken(
                admin.getId(), admin.getUsername(), admin.getRole());
        userToken = jwtTokenProvider.generateToken(
                user.getId(), user.getUsername(), user.getRole());
    }

    @Test
    void shouldAllowAdminAccess() throws Exception {
        mockMvc.perform(get("/api/admin/system")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyUserAccess() throws Exception {
        mockMvc.perform(get("/api/admin/system")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/admin/system"))
                .andExpect(status().isForbidden());
    }
}

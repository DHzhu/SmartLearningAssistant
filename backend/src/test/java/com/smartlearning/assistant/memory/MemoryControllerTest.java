package com.smartlearning.assistant.memory;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smartlearning.assistant.auth.UserPrincipal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class MemoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserPersonaService personaService;

    @InjectMocks
    private MemoryController memoryController;

    private final UserPrincipal testUser = new UserPrincipal(1L, "testuser", "ROLE_USER");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memoryController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter,
                                                  ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest,
                                                  WebDataBinderFactory binderFactory) {
                        return testUser;
                    }
                })
                .build();
    }

    @Test
    void shouldReturnUserMemories() throws Exception {
        UserMemory mem = new UserMemory(1L, "GOAL", "通过六级", 5);
        when(personaService.getUserMemories(1L)).thenReturn(List.of(mem));

        mockMvc.perform(get("/api/memory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("GOAL"))
                .andExpect(jsonPath("$[0].content").value("通过六级"));
    }

    @Test
    void shouldCreateMemory() throws Exception {
        UserMemory mem = new UserMemory(1L, "WEAKNESS", "动态规划", 4);
        when(personaService.saveMemory(1L, "WEAKNESS", "动态规划", 4)).thenReturn(mem);

        mockMvc.perform(post("/api/memory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category\":\"WEAKNESS\",\"content\":\"动态规划\",\"importance\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("WEAKNESS"));
    }

    @Test
    void shouldDeleteMemory() throws Exception {
        doNothing().when(personaService).deleteMemory(100L, 1L);

        mockMvc.perform(delete("/api/memory/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Memory deleted"));
    }
}

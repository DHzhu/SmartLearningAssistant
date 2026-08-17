package com.smartlearning.assistant.llmops;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class AdminMetricsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LlmMetricsService metricsService;

    @InjectMocks
    private AdminMetricsController controller;

    private final UserPrincipal adminUser = new UserPrincipal(1L, "admin", "ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
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
                        return adminUser;
                    }
                })
                .build();
    }

    @Test
    void shouldReturnLlmMetrics() throws Exception {
        LlmMetricsService.AggregatedMetrics mockMetrics = new LlmMetricsService.AggregatedMetrics(
                10L, 100.0, 450.0, 80.0, 5000L, List.of());
        when(metricsService.getAggregatedMetrics()).thenReturn(mockMetrics);

        mockMvc.perform(get("/api/admin/metrics/llm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalls").value(10))
                .andExpect(jsonPath("$.avgTotalLatencyMs").value(450.0));
    }
}

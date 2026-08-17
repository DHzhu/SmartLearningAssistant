package com.smartlearning.assistant.agent;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smartlearning.assistant.auth.UserPrincipal;
import com.smartlearning.assistant.billing.BillingService;
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
class AgentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AgenticChatService agenticChatService;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private AgentController agentController;

    private final UserPrincipal testUser = new UserPrincipal(1L, "testuser", "ROLE_USER");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(agentController)
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
    void shouldReturnChatResponseWhenBalanceSufficient() throws Exception {
        when(billingService.getBalance(1L)).thenReturn(1000L);
        when(agenticChatService.processAgentMessage("你好", 1L))
                .thenReturn(new AgenticChatService.AgentResponse("你好，我是学习助手", List.of("KnowledgeSearchTool"), 15L));

        mockMvc.perform(post("/api/agent/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"你好\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("你好，我是学习助手"))
                .andExpect(jsonPath("$.tokensUsed").value(15));
    }

    @Test
    void shouldReturnInsufficientBalanceWhenZero() throws Exception {
        when(billingService.getBalance(1L)).thenReturn(0L);

        mockMvc.perform(post("/api/agent/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"你好\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_BALANCE"));
    }
}

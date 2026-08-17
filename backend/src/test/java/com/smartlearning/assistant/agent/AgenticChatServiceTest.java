package com.smartlearning.assistant.agent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smartlearning.assistant.agent.tools.AccountTool;
import com.smartlearning.assistant.agent.tools.KnowledgeSearchTool;
import com.smartlearning.assistant.agent.tools.StudyPlannerTool;
import com.smartlearning.assistant.billing.BillingService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgenticChatServiceTest {

    @Mock
    private AccountTool accountTool;
    @Mock
    private KnowledgeSearchTool knowledgeSearchTool;
    @Mock
    private StudyPlannerTool studyPlannerTool;
    @Mock
    private BillingService billingService;

    private AgenticChatService service;

    @BeforeEach
    void setUp() {
        service = new AgenticChatService(
                Optional.empty(),
                accountTool,
                knowledgeSearchTool,
                studyPlannerTool,
                billingService);
    }

    @Test
    void shouldRouteToAccountToolWhenAskingForBalance() {
        when(accountTool.getUserBalance(1L)).thenReturn(new AccountTool.BalanceResponse(1L, 5000L, "余额: 5000"));

        AgenticChatService.AgentResponse res = service.processAgentMessage("查一下我的余额", 1L);

        assertNotNull(res);
        assertTrue(res.toolsUsed().contains("AccountTool"));
        assertEquals("余额: 5000", res.content());
        verify(billingService).deductTokens(1L, 20L);
    }

    @Test
    void shouldRouteToStudyPlannerWhenAskingForPlan() {
        StudyPlannerTool.ScheduleResponse sched = new StudyPlannerTool.ScheduleResponse(
                "复习计划", 90, List.of(new StudyPlannerTool.DailyPlan(1, "基础复习", 90)));
        when(studyPlannerTool.generateStudySchedule(any())).thenReturn(sched);

        AgenticChatService.AgentResponse res = service.processAgentMessage("帮我制定一个复习计划", 1L);

        assertNotNull(res);
        assertTrue(res.toolsUsed().contains("StudyPlannerTool"));
        assertTrue(res.content().contains("基础复习"));
        verify(billingService).deductTokens(1L, 50L);
    }

    @Test
    void shouldRouteToKnowledgeSearchAndFallbackGracefully() {
        when(knowledgeSearchTool.searchKnowledge(any()))
                .thenReturn(new KnowledgeSearchTool.SearchResponse(1, List.of("[file.txt] 内容")));

        AgenticChatService.AgentResponse res = service.processAgentMessage("什么是微积分", 1L);

        assertNotNull(res);
        assertTrue(res.toolsUsed().contains("KnowledgeSearchTool"));
        assertTrue(res.content().contains("[file.txt] 内容"));
        verify(billingService).deductTokens(1L, 10L);
    }
}

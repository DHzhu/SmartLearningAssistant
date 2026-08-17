package com.smartlearning.assistant.agent;

import com.smartlearning.assistant.agent.tools.AccountTool;
import com.smartlearning.assistant.agent.tools.KnowledgeSearchTool;
import com.smartlearning.assistant.agent.tools.StudyPlannerTool;
import com.smartlearning.assistant.billing.BillingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AgenticChatService {

    private static final Logger log = LoggerFactory.getLogger(AgenticChatService.class);

    private final Optional<ChatModel> chatModel;
    private final AccountTool accountTool;
    private final KnowledgeSearchTool knowledgeSearchTool;
    private final StudyPlannerTool studyPlannerTool;
    private final BillingService billingService;

    public AgenticChatService(
            Optional<ChatModel> chatModel,
            AccountTool accountTool,
            KnowledgeSearchTool knowledgeSearchTool,
            StudyPlannerTool studyPlannerTool,
            BillingService billingService) {
        this.chatModel = chatModel;
        this.accountTool = accountTool;
        this.knowledgeSearchTool = knowledgeSearchTool;
        this.studyPlannerTool = studyPlannerTool;
        this.billingService = billingService;
    }

    public record AgentResponse(String content, List<String> toolsUsed, long tokensUsed) {}

    public AgentResponse processAgentMessage(String userMessage, Long userId) {
        List<String> toolsUsed = new ArrayList<>();
        String lower = userMessage.toLowerCase();

        // 1. Tool Intent Routing: Account / Quota / Balance
        if (lower.contains("余额") || lower.contains("balance") || lower.contains("token") || lower.contains("配额")) {
            toolsUsed.add("AccountTool");
            AccountTool.BalanceResponse balanceRes = accountTool.getUserBalance(userId);
            long tokensUsed = 20L;
            billingService.deductTokens(userId, tokensUsed);
            return new AgentResponse(balanceRes.message(), toolsUsed, tokensUsed);
        }

        // 2. Tool Intent Routing: Study Plan & Schedule
        if (lower.contains("计划") || lower.contains("学习计划") || lower.contains("复习") || lower.contains("schedule") || lower.contains("plan")) {
            toolsUsed.add("StudyPlannerTool");
            StudyPlannerTool.ScheduleResponse schedule = studyPlannerTool.generateStudySchedule(
                    new StudyPlannerTool.ScheduleRequest("综合学习计划", 45, 7));
            StringBuilder sb = new StringBuilder();
            sb.append("已为您规划【").append(schedule.subject()).append("】（总计 ").append(schedule.totalMinutes()).append(" 分钟）：\n");
            for (StudyPlannerTool.DailyPlan p : schedule.dailySchedules()) {
                sb.append("• 第 ").append(p.day()).append(" 天: ").append(p.focus()).append(" (").append(p.minutes()).append("分钟)\n");
            }
            long tokensUsed = 50L;
            billingService.deductTokens(userId, tokensUsed);
            return new AgentResponse(sb.toString(), toolsUsed, tokensUsed);
        }

        // 3. Tool Intent Routing: Knowledge Search + LLM Synthesis
        toolsUsed.add("KnowledgeSearchTool");
        KnowledgeSearchTool.SearchResponse searchRes = knowledgeSearchTool.searchKnowledge(
                new KnowledgeSearchTool.SearchRequest(userMessage, userId));

        String synthesizedPrompt = buildAgentPrompt(userMessage, searchRes.documents());

        if (chatModel.isPresent()) {
            try {
                String reply = ChatClient.create(chatModel.get())
                        .prompt()
                        .user(synthesizedPrompt)
                        .call()
                        .content();
                long tokensUsed = (reply.length() + synthesizedPrompt.length()) / 4;
                billingService.deductTokens(userId, tokensUsed);
                return new AgentResponse(reply, toolsUsed, tokensUsed);
            } catch (Exception e) {
                log.error("Agent LLM call failed: {}", e.getMessage());
            }
        }

        // Fallback when LLM not available or error occurs
        String fallbackContent;
        if (searchRes.matchCount() > 0) {
            fallbackContent = "Agent 检索到 " + searchRes.matchCount() + " 条知识库信息：\n" + String.join("\n", searchRes.documents());
        } else {
            fallbackContent = "Agent 未检索到匹配信息，已将问题记录：\"" + userMessage + "\"";
        }
        long tokensUsed = 10L;
        billingService.deductTokens(userId, tokensUsed);
        return new AgentResponse(fallbackContent, toolsUsed, tokensUsed);
    }

    private String buildAgentPrompt(String userMessage, List<String> docChunks) {
        if (docChunks.isEmpty()) {
            return userMessage;
        }
        return "你是一个具备工具调用与精准解答能力的智能学习 Agent。参考以下检索到的资料回答用户问题：\n"
                + String.join("\n", docChunks)
                + "\n\n用户问题：" + userMessage;
    }
}

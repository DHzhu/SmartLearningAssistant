package com.smartlearning.assistant.agent;

import com.smartlearning.assistant.auth.UserPrincipal;
import com.smartlearning.assistant.billing.BillingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgenticChatService agenticChatService;
    private final BillingService billingService;

    public AgentController(AgenticChatService agenticChatService, BillingService billingService) {
        this.agenticChatService = agenticChatService;
        this.billingService = billingService;
    }

    public record AgentChatRequest(@NotBlank(message = "Message cannot be blank") String message) {}

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AgentChatRequest request) {

        long balance = billingService.getBalance(user.userId());
        if (balance <= 0) {
            return ResponseEntity.ok(Map.of(
                    "error", "INSUFFICIENT_BALANCE",
                    "message", "余额不足，请充值"));
        }

        AgenticChatService.AgentResponse response = agenticChatService.processAgentMessage(
                request.message(), user.userId());

        return ResponseEntity.ok(Map.of(
                "content", response.content(),
                "toolsUsed", response.toolsUsed(),
                "tokensUsed", response.tokensUsed()));
    }
}

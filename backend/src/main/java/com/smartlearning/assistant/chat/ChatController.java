package com.smartlearning.assistant.chat;

import com.smartlearning.assistant.auth.UserPrincipal;
import com.smartlearning.assistant.billing.BillingService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private static final long SSE_TIMEOUT = 60000L;

    private final RagService ragService;
    private final BillingService billingService;

    public ChatController(RagService ragService, BillingService billingService) {
        this.ragService = ragService;
        this.billingService = billingService;
    }

    @PostMapping("/stream")
    public SseEmitter stream(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ChatRequest request) {

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        long balance = billingService.getBalance(user.userId());
        if (balance <= 0) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"INSUFFICIENT_BALANCE\",\"message\":\"余额不足，请充值\"}"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        Thread.ofVirtual().name("chat-" + user.userId()).start(() -> {
            try {
                List<Document> context = ragService.retrieveContext(request.message(), user.userId());
                String prompt = ragService.buildPrompt(request.message(), context);

                if (!context.isEmpty()) {
                    List<String> sources = context.stream()
                            .map(doc -> (String) doc.getMetadata().getOrDefault("filename", "unknown"))
                            .distinct()
                            .toList();
                    emitter.send(SseEmitter.event()
                            .name("sources")
                            .data(sources));
                }

                if (ragService.isAvailable()) {
                    String content = ragService.chat(prompt);

                    int chunkSize = 20;
                    for (int i = 0; i < content.length(); i += chunkSize) {
                        int end = Math.min(i + chunkSize, content.length());
                        String chunk = content.substring(i, end);
                        emitter.send(SseEmitter.event()
                                .name("message")
                                .data(chunk));
                        Thread.sleep(50);
                    }

                    // Estimate tokens (rough: 1 token ≈ 4 chars)
                    long estimatedTokens = content.length() / 4 + prompt.length() / 4;
                    billingService.deductTokens(user.userId(), estimatedTokens);
                    log.info("User {} used ~{} estimated tokens", user.userId(), estimatedTokens);
                } else {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data("AI 服务暂未配置。检索到 " + context.size() + " 条相关文档。"));
                }

                emitter.send(SseEmitter.event().name("done").data(""));
                emitter.complete();

            } catch (Exception e) {
                log.error("Chat error for user {}: {}", user.userId(), e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"error\":\"CHAT_ERROR\",\"message\":\"" + e.getMessage() + "\"}"));
                } catch (Exception ignored) {
                    // emitter may already be closed
                }
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> log.warn("SSE timeout for user {}", user.userId()));
        emitter.onError(e -> log.warn("SSE error for user {}: {}", user.userId(), e.getMessage()));

        return emitter;
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncChat(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ChatRequest request) {

        long balance = billingService.getBalance(user.userId());
        if (balance <= 0) {
            return ResponseEntity.ok(Map.of(
                    "error", "INSUFFICIENT_BALANCE",
                    "message", "余额不足，请充值"));
        }

        List<Document> context = ragService.retrieveContext(request.message(), user.userId());
        String prompt = ragService.buildPrompt(request.message(), context);

        if (ragService.isAvailable()) {
            String content = ragService.chat(prompt);

            long estimatedTokens = content.length() / 4 + prompt.length() / 4;
            billingService.deductTokens(user.userId(), estimatedTokens);

            List<String> sources = context.stream()
                    .map(doc -> (String) doc.getMetadata().getOrDefault("filename", "unknown"))
                    .distinct()
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "content", content,
                    "sources", sources,
                    "tokensUsed", estimatedTokens));
        }

        return ResponseEntity.ok(Map.of(
                "content", "AI 服务暂未配置。检索到 " + context.size() + " 条相关文档。",
                "sources", List.of(),
                "tokensUsed", 0));
    }
}

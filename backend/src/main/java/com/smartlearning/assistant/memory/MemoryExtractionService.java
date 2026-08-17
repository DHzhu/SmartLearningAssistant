package com.smartlearning.assistant.memory;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemoryExtractionService {

    public record ExtractedFact(String category, String content, int importance) {}

    public List<ExtractedFact> extractLearnerFacts(String message) {
        if (message == null || message.isBlank()) {
            return List.of();
        }

        List<ExtractedFact> facts = new ArrayList<>();
        String lower = message.toLowerCase();

        // 1. Weakness pattern detection
        if (lower.contains("搞不懂") || lower.contains("不理解") || lower.contains("不太理解")
                || lower.contains("搞混") || lower.contains("不懂") || lower.contains("不会")
                || lower.contains("总是错") || lower.contains("薄弱") || lower.contains("不太会")
                || lower.contains("难点")) {
            facts.add(new ExtractedFact("WEAKNESS", message.trim(), 4));
        }

        // 2. Goal & Target pattern detection
        if (lower.contains("目标是") || lower.contains("打算考") || lower.contains("准备备考")
                || lower.contains("计划在") || lower.contains("拿到") || lower.contains("希望达到")
                || lower.contains("想要学好")) {
            facts.add(new ExtractedFact("GOAL", message.trim(), 5));
        }

        // 3. Preference pattern detection
        if (lower.contains("喜欢用") || lower.contains("偏好") || lower.contains("习惯在")
                || lower.contains("希望用简洁") || lower.contains("多给代码示例")) {
            facts.add(new ExtractedFact("PREFERENCE", message.trim(), 3));
        }

        return facts;
    }
}

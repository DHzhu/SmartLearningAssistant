package com.smartlearning.assistant.memory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryExtractionServiceTest {

    private MemoryExtractionService extractionService;

    @BeforeEach
    void setUp() {
        extractionService = new MemoryExtractionService();
    }

    @Test
    void shouldExtractWeaknessFromConversation() {
        String msg = "我不太理解红黑树的旋转机制，总是搞混。";
        List<MemoryExtractionService.ExtractedFact> facts = extractionService.extractLearnerFacts(msg);

        assertFalse(facts.isEmpty());
        assertEquals("WEAKNESS", facts.get(0).category());
        assertTrue(facts.get(0).content().contains("红黑树"));
    }

    @Test
    void shouldExtractGoalFromConversation() {
        String msg = "我的目标是今年通过考研数学一并拿到 120 分。";
        List<MemoryExtractionService.ExtractedFact> facts = extractionService.extractLearnerFacts(msg);

        assertFalse(facts.isEmpty());
        assertEquals("GOAL", facts.get(0).category());
        assertTrue(facts.get(0).content().contains("考研数学"));
    }

    @Test
    void shouldReturnEmptyWhenNoSpecialFacts() {
        String msg = "你好，今天天气真不错。";
        List<MemoryExtractionService.ExtractedFact> facts = extractionService.extractLearnerFacts(msg);

        assertTrue(facts.isEmpty());
    }
}

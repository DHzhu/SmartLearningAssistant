package com.smartlearning.assistant.knowledge.chunking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SemanticChunkerTest {

    private SemanticChunker chunker;

    @BeforeEach
    void setUp() {
        chunker = new SemanticChunker();
    }

    @Test
    void shouldSplitByParagraphsWhenFittingChunkSize() {
        String text = "段落一：Java是一门面向对象编程语言。\n\n段落二：Spring Boot极大地简化了微服务开发。\n\n段落三：PostgreSQL是一款强大的关系型数据库。";
        List<String> chunks = chunker.chunkText(text, 100, 20);

        assertFalse(chunks.isEmpty());
        assertTrue(chunks.size() <= 3);
        assertTrue(chunks.get(0).contains("段落一"));
    }

    @Test
    void shouldSplitLongParagraphsOnSentences() {
        String longText = "第一句话介绍基础知识。第二句话讲解进阶特性。第三句话总结核心实践。第四句话布置复习作业。";
        List<String> chunks = chunker.chunkText(longText, 25, 5);

        assertTrue(chunks.size() >= 2);
        for (String chunk : chunks) {
            assertFalse(chunk.isBlank());
        }
    }

    @Test
    void shouldHandleEmptyOrBlankText() {
        List<String> chunks = chunker.chunkText("   ", 100, 20);
        assertTrue(chunks.isEmpty());
    }
}

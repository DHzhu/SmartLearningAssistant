package com.smartlearning.assistant.chat.rerank;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class RerankerServiceTest {

    private RerankerService reranker;

    @BeforeEach
    void setUp() {
        reranker = new RerankerService();
    }

    @Test
    void shouldRankExactKeywordMatchHigher() {
        Document doc1 = new Document("这是一篇关于数据库索引的泛泛介绍。", Map.of("id", "1"));
        Document doc2 = new Document("Spring AI 提供了 ChatClient 和 Embedding 支持，极大方便了 RAG 开发。", Map.of("id", "2"));
        Document doc3 = new Document("关于计算机操作系统的内存调度原理。", Map.of("id", "3"));

        List<Document> candidates = List.of(doc1, doc2, doc3);
        List<Document> result = reranker.rerank("Spring AI ChatClient", candidates, 2);

        assertEquals(2, result.size());
        assertEquals("2", result.get(0).getMetadata().get("id"));
    }

    @Test
    void shouldHandleEmptyCandidates() {
        List<Document> result = reranker.rerank("test query", List.of(), 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRespectTopKLimit() {
        Document doc1 = new Document("Java 线程安全与并发", Map.of("id", "1"));
        Document doc2 = new Document("Java 垃圾回收机制", Map.of("id", "2"));
        Document doc3 = new Document("Java 集合类源码分析", Map.of("id", "3"));

        List<Document> candidates = List.of(doc1, doc2, doc3);
        List<Document> result = reranker.rerank("Java", candidates, 1);

        assertEquals(1, result.size());
    }
}

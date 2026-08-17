package com.smartlearning.assistant.infra.queue;

import com.smartlearning.assistant.knowledge.VectorizationService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeTaskConsumer {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeTaskConsumer.class);
    public static final String TOPIC = "KNOWLEDGE_VECTORIZATION_TASK";

    private final AsyncEventQueue eventQueue;
    private final VectorizationService vectorizationService;

    public KnowledgeTaskConsumer(AsyncEventQueue eventQueue, VectorizationService vectorizationService) {
        this.eventQueue = eventQueue;
        this.vectorizationService = vectorizationService;
    }

    @PostConstruct
    public void init() {
        eventQueue.subscribe(TOPIC, this::handleEvent);
    }

    public void handleEvent(EventMessage<?> msg) {
        if (msg.payload() instanceof Long taskId) {
            log.info("Consumer received vectorization taskId: {}", taskId);
            vectorizationService.processTask(taskId);
        }
    }
}

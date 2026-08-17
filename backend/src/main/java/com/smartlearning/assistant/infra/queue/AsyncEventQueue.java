package com.smartlearning.assistant.infra.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AsyncEventQueue {

    private static final Logger log = LoggerFactory.getLogger(AsyncEventQueue.class);

    private final BlockingQueue<EventMessage<?>> queue = new LinkedBlockingQueue<>(10000);
    private final ConcurrentHashMap<String, Consumer<EventMessage<?>>> topicConsumers = new ConcurrentHashMap<>();

    public <T> boolean publish(String topic, T payload) {
        EventMessage<T> message = EventMessage.of(topic, payload);
        boolean offered = queue.offer(message);
        if (offered) {
            log.debug("Published event {} to topic {}", message.eventId(), topic);
            processNext();
        } else {
            log.warn("Event queue full, dropped message on topic {}", topic);
        }
        return offered;
    }

    public void subscribe(String topic, Consumer<EventMessage<?>> consumer) {
        topicConsumers.put(topic, consumer);
    }

    public void processNext() {
        EventMessage<?> message = queue.poll();
        if (message != null) {
            Consumer<EventMessage<?>> consumer = topicConsumers.get(message.topic());
            if (consumer != null) {
                try {
                    consumer.accept(message);
                } catch (Exception e) {
                    log.error("Failed processing event {} on topic {}: {}", message.eventId(), message.topic(), e.getMessage(), e);
                }
            }
        }
    }

    public int getPendingCount() {
        return queue.size();
    }
}

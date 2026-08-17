package com.smartlearning.assistant.infra.queue;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventMessage<T>(
        String eventId,
        String topic,
        T payload,
        LocalDateTime timestamp) {

    public static <T> EventMessage<T> of(String topic, T payload) {
        return new EventMessage<>(UUID.randomUUID().toString(), topic, payload, LocalDateTime.now());
    }
}

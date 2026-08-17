package com.smartlearning.assistant.infra.queue;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncEventQueueTest {

    private AsyncEventQueue queue;

    @BeforeEach
    void setUp() {
        queue = new AsyncEventQueue();
    }

    @Test
    void shouldPublishAndConsumeEvent() {
        AtomicReference<String> received = new AtomicReference<>();
        queue.subscribe("TEST_TOPIC", msg -> received.set((String) msg.payload()));

        boolean published = queue.publish("TEST_TOPIC", "Hello Event");

        assertTrue(published);
        assertEquals("Hello Event", received.get());
    }

    @Test
    void shouldDropGracefullyWhenNoConsumerRegistered() {
        boolean published = queue.publish("UNREGISTERED_TOPIC", 12345L);
        assertTrue(published);
    }
}

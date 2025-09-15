package org.homework;

import org.homework.impl.*;
import org.homework.itf.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.homework.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

class EventBusV1Test {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testEventBus() {
        EventBus eventBus = new EventBusImplV1();
        eventBus.addSubscriber(EventString.class, new Subscriber<>("sub1", getLoggingConsumer()));
        eventBus.addSubscriber(EventSum.class, new Subscriber<>("sub2", getSumConsumer()));

        long begin = System.nanoTime();
        eventBus.publishEvent(new EventString("Test string"));
        IntStream.range(0, 1_000_000).forEach(i -> eventBus.publishEvent(new EventSum(i, i+1)));
        logTimeConsumed(begin);
    }

    @Test
    void testEventBusException() {
        EventBus eventBus = new EventBusImplV1();
        eventBus.addSubscriber(EventDouble.class, new Subscriber<>("sub1", getSumConsumer()));
        assertThrows(ClassCastException.class, () -> eventBus.publishEvent(new EventDouble(200d)));
    }
}
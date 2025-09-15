package org.homework;

import org.homework.impl.*;
import org.homework.itf.Event;
import org.homework.itf.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.callback.Callback;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.homework.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventBusV2Test {

    ExecutorService publisher = Executors.newScheduledThreadPool(64);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testEventBus() throws InterruptedException {
        EventBus eventBus = new EventBusImplV2(64, Integer.MAX_VALUE);
        eventBus.addSubscriber(EventString.class, new Subscriber<>("sub1", getLoggingConsumer()));
        eventBus.addSubscriber(EventSum.class, new Subscriber<>("sub2", getSumConsumer()));

        long begin = System.nanoTime();
        /**
         * Multi thread publish event
         */
        publisher.execute(() -> eventBus.publishEvent(new EventString("Test string")));
        IntStream.range(0, 1_000_000).forEach(i -> publisher.execute(() -> eventBus.publishEvent(new EventSum(i, i + 1))));
        //TODO : improve log
        logTimeConsumed(begin);
        Thread.sleep(10_000);
    }

    @Test
    void testSubscribeSuperClassEventBus() {
        EventBus eventBus = new EventBusImplV2(16, 100);
        eventBus.addSubscriber(Event.class, new Subscriber<>("sub1", getSumConsumer()));
        publisher.execute(() -> eventBus.publishEvent(new EventDouble(200d)));//no log
        publisher.execute(() -> eventBus.publishEvent(new EventSum(200, 100)));//compatible type with log
        publisher.execute(() -> eventBus.publishEvent(new EventString("test")));//no log
    }
}
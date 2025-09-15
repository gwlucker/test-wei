package org.homework;

import org.homework.impl.EventString;
import org.homework.impl.Subscriber;
import org.homework.impl.ThrottlerImpl;
import org.homework.itf.Throttler;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.homework.TestUtil.*;

public class ThrottlerTest {

    ExecutorService executor = Executors.newScheduledThreadPool(64);


    @Test
    public void testPollMode() throws InterruptedException {
        //Throttler with 5 times / 1000ms
        ThrottlerImpl throttler = new ThrottlerImpl(5, 1000);
        Subscriber<EventString> subscriber = new Subscriber<>("sub", getLoggingConsumer());
        Subscriber<EventString> subscriber2 = new Subscriber<>("sub2", getLoggingConsumer());
        AtomicInteger count = new AtomicInteger();

        //Use 2 threads to poll
        executor.execute(() -> {
            while (true) {
                if (throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                    subscriber.consume(new EventString("Msg1"));
                    count.getAndIncrement();
                }
            }
        });

        executor.execute(() -> {
            while (true) {
                if (throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                    subscriber2.consume(new EventString("Msg2"));
                    count.getAndIncrement();
                }
            }
        });
        executor.shutdown();
        executor.awaitTermination(800, TimeUnit.MILLISECONDS);
        //under window, only max can be extracted
        Assert.assertEquals(5, count.getAcquire());
    }

    //TODO : find a proper way to test push mode result
    @Test
    public void testPushMode() throws InterruptedException {
        Subscriber<EventString> subscriber = new Subscriber<>("sub", getLoggingConsumer());

        int frequencyMax = 5;
        int timePeriod = 1000;
        ThrottlerImpl throttler = new ThrottlerImpl(frequencyMax, timePeriod);
        for (int i = 0; i <= frequencyMax; i++) {
            throttler.notifyWhenCanProceed(subscriber, new EventString("Test"));
        }

        long before = System.nanoTime();
        //Use threads to poll
        for (int i = 0; i <= frequencyMax; i++) {
            executor.execute(() -> {
                logTimeConsumed(before);
                throttler.notifyWhenCanProceed(subscriber, new EventString("Test p2"));
            });
        }
        executor.shutdown();
        executor.awaitTermination(5000L, TimeUnit.MILLISECONDS);
        Thread.sleep(5000L);
        //TODO : find assert condition if possible

    }
}

package org.homework;

import org.homework.impl.*;
import org.homework.itf.ProbabilisticRandomGen;
import org.homework.itf.SlidingWindowStatistics;
import org.homework.itf.Throttler;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.homework.TestUtil.*;

public class StatisticsTest {

    ExecutorService executor = Executors.newScheduledThreadPool(64);

    @Test
    public void test() throws InterruptedException {
        EventBusImplV2 eventBus = new EventBusImplV2(64, 500);
        TimeBasedSlidingWindowStatistics statistics = new TimeBasedSlidingWindowStatistics(1000, eventBus);
        Subscriber<SlidingWindowStatistics.Statistics> sub = new Subscriber<>("sub", consumeStatistic());
        statistics.subscribeForStatistics(sub, filter -> filter.getMean().doubleValue() > 51d);

        //TODO : add a real test case to check return value
        Random r = new Random();
        for(int i = 0; i < 10; i++){
            executor.execute(() -> {
                while (true) {
                    statistics.add(new LatencyMeasurement(r.nextInt(100), System.currentTimeMillis()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

}

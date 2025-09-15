package org.homework.impl;

import org.homework.itf.Throttler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThrottlerImpl implements Throttler {

    //how many times something can happen
    private final int frequency;
    //during a period, in ms
    private final long timePeriod;
    private final List<Long> timestamps = new LinkedList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ThrottlerImpl(int frequency, long timePeriod) {
        this.frequency = frequency;
        this.timePeriod = timePeriod;
    }

    @Override
    public ThrottleResult shouldProceed() {
        long now = System.currentTimeMillis();
        ThrottleResult result;
        synchronized (timestamps) {//thread safe on consume counter
            removeExpiredTimestamps(now);
            if (timestamps.size() <= frequency) {
                timestamps.add(now);//we suppose that once poll mode get the proceed token, it will proceed
                result = ThrottleResult.PROCEED;
            } else {
                result = ThrottleResult.DO_NOT_PROCEED;
            }
        }
        return result;
    }

    @Override
    public void notifyWhenCanProceed(Subscriber subscriber, Object object) {
        long nextSlot;
        long now;
        synchronized (this) {
            now = System.currentTimeMillis();
            removeExpiredTimestamps(now);
            if (timestamps.size() < frequency) {
                timestamps.add(now);//add timestamp when we proceed
                subscriber.consume(object);
                return;
            }
            nextSlot = timestamps.getFirst() + timePeriod;
        }
        long delay = Math.max(0, nextSlot - now);
        scheduler.schedule(() -> notifyWhenCanProceed(subscriber, object), delay, TimeUnit.MILLISECONDS);
    }

    /**
     * remove out-dated timestamps
     */
    private void removeExpiredTimestamps(long now) {
        long previousSlot = now - timePeriod;
        timestamps.removeIf(timestamp -> timestamp <= previousSlot);
    }
}

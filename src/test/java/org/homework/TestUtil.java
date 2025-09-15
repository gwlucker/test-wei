package org.homework;

import org.homework.impl.EventSum;
import org.homework.impl.EventString;
import org.homework.itf.SlidingWindowStatistics;

import java.util.function.Consumer;

public class TestUtil {

    private TestUtil() {
    }

    public static Consumer<EventString> getLoggingConsumer() {
        return s -> System.out.println(String.format("[LOG] %s : %s -> %s ", Thread.currentThread().getName(), s.getClass(), s.value()));
    }

    public static Consumer<SlidingWindowStatistics.Statistics> consumeStatistic() {
        return s -> System.out.println(String.format("[LOG] %s : %s", Thread.currentThread().getName(), s.toString()));
    }

    public static Consumer<EventSum> getSumConsumer() {
        return s -> System.out.println(String.format("%s : %s + %s = %s", Thread.currentThread().getName(), s.a(), s.b(), s.sum()));
    }

    public static Consumer<EventString> doNothing() {
        return _ -> {
            //do nothing
        };
    }

    public static void logTimeConsumed(long begin) {
        System.out.println("Time consumed " + (System.nanoTime() - begin) / 1000 + "us");
    }
}

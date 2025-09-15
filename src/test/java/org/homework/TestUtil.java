package org.homework;

import org.homework.impl.EventSum;
import org.homework.impl.EventString;

import java.util.function.Consumer;

public class TestUtil {

    private TestUtil() {
    }

    public static Consumer<EventString> getLoggingConsumer() {
        return s -> System.out.println(String.format("[LOG] %s : %s -> %s ", Thread.currentThread().getName(), s.getClass(), s.value()));
    }

    public static Consumer<EventSum> getSumConsumer() {
        return s -> System.out.println(String.format("%s : %s + %s = %s", Thread.currentThread().getName(), s.a(), s.b(), s.sum()));
    }

    public static void logTimeConsumed(long begin) {
        System.out.println("Time consumed " + (System.nanoTime() - begin) / 1000 + "us");
    }
}

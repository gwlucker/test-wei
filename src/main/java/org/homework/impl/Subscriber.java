package org.homework.impl;

import java.util.function.Consumer;

/**
 * a subscriber consume an event
 * @param <T> event type
 */
public class Subscriber<T> {
    String name;
    Consumer<T> consumer;

    public Subscriber(String name, Consumer<T> consumer) {
        this.name = name;
        this.consumer = consumer;
    }

    public void consume(T t) {
        consumer.accept(t);
    }
}

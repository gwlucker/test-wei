package org.homework.impl;

import org.homework.itf.EventBus;
import org.homework.itf.EventFilter;

import java.util.*;

/**
 * implementation V1 : single thread case
 */
public class EventBusImplV1 implements EventBus {

    protected final Map<Class, Set<SubscriberWithFilter>> subscribers;

    public EventBusImplV1() {
        this.subscribers = new HashMap<>();
    }

    protected EventBusImplV1(Map<Class, Set<SubscriberWithFilter>> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public void publishEvent(Object o) {
        Set<SubscriberWithFilter> set = subscribers.get(o.getClass());
        if (null == set) {
            subscribers.entrySet().forEach(entry -> {
                if(entry.getKey().isAssignableFrom(o.getClass())) {
                    consumeAll(o, entry.getValue());
                }
            });
        } else {
            consumeAll(o, set);
        }
    }

    protected void consumeAll(Object o, Set<SubscriberWithFilter> set) {
        set.forEach(sw -> {
            if (sw.filter().test(o)) {
                sw.subscriber().consume(o);
            }
        });
    }

    @Override
    public void addSubscriber(Class<?> clazz, Subscriber subscriber) {
        subscribers.computeIfAbsent(clazz, k -> new HashSet<>()).add(new SubscriberWithFilter(subscriber, AcceptAllFilter.INSTANCE));
    }

    @Override
    public void addSubscriberForFilteredEvents(Class<?> clazz, Subscriber subscriber, EventFilter<?> filter) {
        subscribers.computeIfAbsent(clazz, k -> new HashSet<>()).add(new SubscriberWithFilter(subscriber, filter));
    }
}

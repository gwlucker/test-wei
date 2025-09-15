package org.homework.itf;

import org.homework.impl.Subscriber;

/** MANDATORY
 * An event bus is similar to a messaging daemon – you publish messages to it, and subscribers receive those messages.
 * Write a version of the EventBus designed for single-threaded use (thread calling publishEvent is the same as the thread used for the callback on the
 * subscriber).
 * Then write a version of the EventBus which supports multiple producers and multiple consumers (thread calling publishEvent is different to thread making
 * the callback)
 * Do not use any non JDK libraries for the multithreading.Extra points if you can extend the multithreaded version (maybe by extending the interface) so it supports event types where only the latest value matters
 * (coalescing / conflation) – i.e. multiple market data snapshots arriving while the algo is processing an update.
 *
 * public interface EventBusV2 {
 *     // Feel free to replace Object with something more specific,
 *     // but be prepared to justify it
 *     void publishEvent(Object o);
 *     // How would you denote the subscriber?
 *     void addSubscriber(Class<?> clazz, ??);
 *     // Would you allow clients to filter the events they receive? How would the interface look like?
 *     void addSubscriberForFilteredEvents(????);
 * }
 */
public interface EventBus {
    // Feel free to replace Object with something more specific,
    // but be prepared to justify it
    /**
     * TODO : can be specified with a generic type if necessary, EventBus<T> to build a MainEventBus/SubEventBus tree like structure, with a strict
     * @param o
     */
    void publishEvent(Object o);

    // How would you denote the subscriber?
    /**
     * a subscriber listen to all event type of clazz
     * @param clazz
     * @param subscriber
     */
    void addSubscriber(Class<?> clazz, Subscriber subscriber);

    // Would you allow clients to filter the events they receive? How would the interface look like?
    void addSubscriberForFilteredEvents(Class<?> clazz, Subscriber subscriber, EventFilter<?> filter);
}

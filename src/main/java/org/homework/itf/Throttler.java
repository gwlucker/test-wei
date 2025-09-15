package org.homework.itf;


import org.homework.impl.Subscriber;

/** MANDATORY
 * A Throttler allows a client to restrict how many times something can happen in a given time period (for example we may not want to send more than a
 * number of quotes to an exchange in a specific time period).
 * It should be possible to ask it if we can proceed with the throttled operation, as well as be notified by it.
 * Do not assume thread safety in the subscriber.
 */
public interface Throttler {
    // check if we can proceed (poll)
    ThrottleResult shouldProceed();

    // subscribe to be told when we can proceed (Push)
    void notifyWhenCanProceed(Subscriber subscriber, Object object);

    enum ThrottleResult {
        PROCEED, // publish, aggregate etc
        DO_NOT_PROCEED //
    }
}

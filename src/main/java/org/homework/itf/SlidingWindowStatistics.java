package org.homework.itf;


import org.homework.impl.Subscriber;

/** OPTIONAL
 * Write a general purpose class that calculates statistics about a sequence of integers.
 * An example use case would be latency statistics – you can add latency measurements to it and get statistics out.
 * Some clients may only want to be notified when the statistics match certain criteria – e.g. the mean has gone above a threshold.
 * Do not assume that subscribers will be threadsafe. The add method may be called by a different thread than the callback should be made on.
 */
public interface SlidingWindowStatistics<N> {
    void add(N measurement);

    // subscriber will have a callback that'll deliver a Statistics instance (push)
    void subscribeForStatistics(Subscriber<Statistics> subscriber, EventFilter<Statistics> filter);

    // get latest statistics (poll)
    Statistics getLatestStatistics();

    public interface Statistics<T extends Number> {
        T getMean();

        T getMode();

        T getPctile(int pctile);
    }
}

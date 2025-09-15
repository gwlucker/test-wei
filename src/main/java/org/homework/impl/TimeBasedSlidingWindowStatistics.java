package org.homework.impl;

import org.homework.itf.EventBus;
import org.homework.itf.EventFilter;
import org.homework.itf.SlidingWindowStatistics;

import java.util.*;

/**
 * Time based
 * Sliding window can be time base(for last x second) or Nb based(last x elements)
 */
public class TimeBasedSlidingWindowStatistics implements SlidingWindowStatistics<LatencyMeasurement> {

    private final LinkedList<LatencyMeasurement> elements;//for thread safe
    private final long windowsSize;
    private StatImpl stat;
    private double sum = 0d;//cached sum
    private final Map<Integer, Integer> frequencyMap = new HashMap<>();//cached mode
    private EventBus eventBus;

    public TimeBasedSlidingWindowStatistics(long windowsSize, EventBus eventBus) {
        this.windowsSize = windowsSize;
        this.eventBus = eventBus;
        this.elements = new LinkedList<>();
    }

    @Override
    public synchronized void add(LatencyMeasurement measurement) {
        //add new element
        elements.add(measurement);
        Integer latency = measurement.latency();
        sum += latency;
        frequencyMap.put(latency, frequencyMap.getOrDefault(latency, 0) + 1);
        //remove old element
        removeOutOfWindow();
        //calculate stat
        List<Integer> list = elements.stream().map(LatencyMeasurement::latency).sorted().toList();
        double mean = sum / list.size();
        stat = new StatImpl(mean, computeMode(), list);
        eventBus.publishEvent(stat);
    }

    @Override
    public void subscribeForStatistics(Subscriber<Statistics> subscriber, EventFilter<Statistics> filter) {
        eventBus.addSubscriberForFilteredEvents(Statistics.class, subscriber, filter);
    }

    @Override
    public Statistics getLatestStatistics() {
        return stat;
    }


    //clean element that passed the statistic window
    private void removeOutOfWindow() {
        Iterator<LatencyMeasurement> iter = elements.iterator();
        long now = System.currentTimeMillis();
        while (iter.hasNext()) {
            LatencyMeasurement next = iter.next();
            if (now - next.timestamp() > windowsSize) {
                Integer latency = next.latency();
                sum -= latency;
                frequencyMap.put(latency, frequencyMap.get(latency) - 1);
                if (frequencyMap.get(latency) == 0) {
                    frequencyMap.remove(latency);
                }
                iter.remove();
            } else {
                break;//after current iter, timestamp is in the window
            }
        }
    }

    //calculate Mode from frequency map
    private int computeMode() {
        int mode = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount || (entry.getValue() == maxCount && entry.getKey() < mode)) {
                mode = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mode;
    }

    //TODO : optimize statistic creation with intermediary result, i.e. move
    private class StatImpl implements Statistics<Double> {
        private double mean;
        private double mode;
        private List<Integer>  sortedList;

        public StatImpl(double mean, double mode, List<Integer> sortedList) {
            this.mean = mean;
            this.mode = mode;
            this.sortedList = sortedList;
        }

        @Override
        public Double getMean() {
            return mean;
        }

        @Override
        public Double getMode() {
            return mode;
        }

        @Override
        public Double getPctile(int pctile) {
            if (sortedList.isEmpty()) {
                return null;//no percentil if list is empty
            }
            if (pctile < 0 || pctile > 100) {
                throw new IllegalArgumentException("Invalid pctile, value should be in [0,100]");
            }
            int index = Math.max((int) Math.ceil((pctile / 100d) * sortedList.size()) - 1, 0);
            return sortedList.get(index).doubleValue();
        }

        @Override
        public String toString() {
            return "StatImpl{" +
                    "mean=" + mean +
                    ", mode=" + mode +
                    '}';
        }
    }
}

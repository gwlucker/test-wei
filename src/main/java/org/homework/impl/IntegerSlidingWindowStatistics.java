package org.homework.impl;

import org.homework.itf.SlidingWindowStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class IntegerSlidingWindowStatistics implements SlidingWindowStatistics<Integer> {

    private Vector<Integer> elements;//for thread safe

    public IntegerSlidingWindowStatistics() {
        this.elements = new Vector<>();
    }

    @Override
    public void add(Integer measurement) {
        elements.add(measurement);
    }

    @Override
    public void subscribeForStatistics(Subscriber<Statistics> subscriber) {
        subscriber.consume(getLatestStatistics());
    }

    @Override
    public Statistics getLatestStatistics() {
        return new StatImpl(List.copyOf(this.elements));
    }

    private class StatImpl implements Statistics<Double> {
        private final List<Integer> sortedList;
        private final static Double ZERO = Double.valueOf(0d);

        public StatImpl(List<Integer> list) {
            this.sortedList = list.stream().sorted().toList();
        }

        @Override
        public Double getMean() {
            if (sortedList.isEmpty()) {
                return ZERO;//FIXME :  zero or null?
            }
            return sortedList.stream().reduce(0, Integer::sum).doubleValue() / sortedList.size();
        }

        @Override
        public Double getMode() {
            return 0.0;
        }

        @Override
        public Double getPctile(int pctile) {
            if (sortedList.isEmpty()) {
                return null;//no percentil if list is empty
            }
            if (pctile < 0 || pctile > 100) {
                throw new IllegalArgumentException("Invalid pctile, value should be in [0,100]");
            }
            int index = (int) Math.ceil((pctile / 100d) * sortedList.size()) - 1;
            index = Math.max(0, index);

        }
    }
}

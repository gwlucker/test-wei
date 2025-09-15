package org.homework.impl;

import org.homework.itf.EventFilter;

import java.util.Arrays;
import java.util.List;

/**
 * a composite filter
 */
public class CompositeFilter implements EventFilter {

    private final List<EventFilter> filters;

    public CompositeFilter(EventFilter... filters) {
        this.filters = Arrays.stream(filters).toList();
    }

    @Override
    public boolean test(Object o) {
        return filters.stream().allMatch(f -> f.test(o));
    }
}

package org.homework.impl;

import org.homework.itf.EventFilter;

/**
 * Filter that accept all events
 */
public class AcceptAllFilter implements EventFilter {

    public static AcceptAllFilter INSTANCE = new AcceptAllFilter();

    private AcceptAllFilter() {
    }

    @Override
    public boolean test(Object o) {
        return true;
    }
}

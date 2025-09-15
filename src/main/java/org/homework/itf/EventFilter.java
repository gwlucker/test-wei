package org.homework.itf;

import java.util.function.Predicate;

/**
 * Event filter : currently extends from predicate
 * TODO : change to other type if necessary
 * @param <T>
 */
@FunctionalInterface
public interface EventFilter<T> extends Predicate<T> {

    /**
     * event filter accept condition
     * @param t input event
     * @return true if t should be accepted, false if t should be filtered
     */
    @Override
    boolean test(T t);
}

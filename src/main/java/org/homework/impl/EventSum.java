package org.homework.impl;

import org.homework.itf.Event;

public record EventSum(Integer a, Integer b) implements Event {

    public Integer sum() {
        return a + b;
    }
}

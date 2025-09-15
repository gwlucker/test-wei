package org.homework.impl;

import org.homework.itf.EventFilter;

public record SubscriberWithFilter(Subscriber subscriber, EventFilter filter) {
}
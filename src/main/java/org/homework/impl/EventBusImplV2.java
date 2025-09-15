package org.homework.impl;

import java.util.*;
import java.util.concurrent.*;

/**
 * implem V2 : multi thread model, extends from V1
 */
public class EventBusImplV2 extends EventBusImplV1 {

    private final ExecutorService executor;

    public EventBusImplV2(final int poolSize, final int queueSize) {
        super(new ConcurrentHashMap<>());
        /**
         * a fixed thread pool executor, with limited queue
         * or use non jdk lib(disruptor for example)
         */
        this.executor = new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize));
    }

    @Override
    protected void consumeAll(Object o, Set<SubscriberWithFilter> set) {
        set.forEach(sw -> executor.submit(() -> {
            if (sw.filter().test(o)) {
                sw.subscriber().consume(o);
            }
        }));
    }
}

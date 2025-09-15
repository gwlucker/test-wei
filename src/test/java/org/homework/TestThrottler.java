package org.homework;

import org.homework.impl.EventString;
import org.homework.impl.Subscriber;
import org.homework.impl.ThrottlerImpl;
import org.homework.itf.Throttler;
import org.junit.jupiter.api.Test;

import static org.homework.TestUtil.getLoggingConsumer;

public class TestThrottler {

    @Test
    public void testPollMode() {
        //Throttler with 5 times / 1000ms
        ThrottlerImpl throttler = new ThrottlerImpl(5, 1000);
        Subscriber<EventString> subscriber = new Subscriber<>("sub", getLoggingConsumer());
        while (true) {
            if (throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                subscriber.consume(new EventString("Msg consumed"));
            }
        }
    }

    @Test
    public void testPushMode(){


    }
}

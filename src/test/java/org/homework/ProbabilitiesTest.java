package org.homework;

import org.homework.impl.ProbabilisticRandomGenImpl;
import org.homework.itf.ProbabilisticRandomGen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProbabilitiesTest {

    @Test
    void testProbabilities() {
        List<ProbabilisticRandomGen.NumAndProbability> elements = Arrays.asList(
                new ProbabilisticRandomGen.NumAndProbability(2, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(3, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(5, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(7, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(11, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(13, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(17, 0.2f),
                new ProbabilisticRandomGen.NumAndProbability(19, 0.2f)
        );
        /*
        System.out.println("Theory probabilities :");
        elements.forEach(e -> {
            System.out.println(e.getNumber() + " = " + e.getProbabilityOfSample());
        });
         */

        ProbabilisticRandomGenImpl val = new ProbabilisticRandomGenImpl(elements);
        ExecutorService exec = Executors.newFixedThreadPool(100);
        int itr = 1_000_000;//nb of iteration
        Map<Integer, AtomicInteger> count = new TreeMap<>();
        elements.forEach(e -> count.put(e.getNumber(), new AtomicInteger(0)));
        for (int i = 0; i < itr; i++) {
            exec.execute(() -> count.get(val.nextFromSample()).incrementAndGet());
        }
        exec.shutdown();
        try {
            exec.awaitTermination(10, TimeUnit.SECONDS);
            //show statistic result after itr runs
            /*
            System.out.println("Run for " + itr + " samples");
            count.entrySet().forEach(e -> {
                System.out.println(e.getKey() + " = " + e.getValue().get() * 1d / itr);
            });
             */
            //check result between theory and sampling
            Iterator<Map.Entry<Integer, AtomicInteger>> sampleIter = count.entrySet().iterator();
            Iterator<ProbabilisticRandomGen.NumAndProbability> theoryIter = elements.iterator();
            double delta = 1e2;
            while (sampleIter.hasNext() && theoryIter.hasNext()) {
                float sample = sampleIter.next().getValue().get() * 1f / itr;
                ProbabilisticRandomGen.NumAndProbability theory = theoryIter.next();
                Assertions.assertTrue(Math.abs(theory.getProbabilityOfSample() - sample) < delta, String.format("%s : Theory probability %s not equals to sample probability %s", theory.getNumber(), theory.getProbabilityOfSample(), sample));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testErrorNegaProba() {
        //case probability < 0f
        List<ProbabilisticRandomGen.NumAndProbability> elements = Arrays.asList(
                new ProbabilisticRandomGen.NumAndProbability(2, 0.5f),
                new ProbabilisticRandomGen.NumAndProbability(3, 0.5f),
                new ProbabilisticRandomGen.NumAndProbability(5, 0.5f),
                new ProbabilisticRandomGen.NumAndProbability(7, -0.5f)
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ProbabilisticRandomGenImpl(elements));
    }

    @Test
    void testErrorSum() {
        //case sum of probability != 1f
        List<ProbabilisticRandomGen.NumAndProbability> elements = Arrays.asList(
                new ProbabilisticRandomGen.NumAndProbability(2, 0.1f),
                new ProbabilisticRandomGen.NumAndProbability(3, 0.1f)
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ProbabilisticRandomGenImpl(elements));
    }
}

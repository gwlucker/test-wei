package org.homework.impl;

import org.homework.itf.ProbabilisticRandomGen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ProbabilisticRandomGenImpl implements ProbabilisticRandomGen {

    private final List<NumAndProbability> elements;
    //cumulated probabilities
    private final float[] cumulatedProb;
    //randomizer
    private final Random random;

    public ProbabilisticRandomGenImpl(List<NumAndProbability> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException("Empty list");
        }
        //check sum should be 1.0
        Float sum = elements.stream().map(e -> e.getProbabilityOfSample()).reduce(0f, Float::sum);
        if (sum != 1.0f) {
            //TODO : normalization?
            throw new IllegalArgumentException("Sum of probability is not 1.0");
        }
        this.elements = List.copyOf(elements);
        this.cumulatedProb = cumulateProbabilities();
        this.random = new Random();
    }

    @Override
    public int nextFromSample() {
        //generate next
        float randVal = random.nextFloat();
        // Binary search to find the appropriate index
        int left = 0;
        int right = cumulatedProb.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (randVal < cumulatedProb[mid]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return elements.get(left).getNumber();
    }

    private float[] cumulateProbabilities() {
        float[] cumulated = new float[elements.size()];
        float sum = 0f;
        Iterator<NumAndProbability> iter = elements.iterator();
        int idx = 0;
        while (iter.hasNext()) {
            final NumAndProbability item = iter.next();
            if (item.getProbabilityOfSample() < 0) {
                throw new IllegalArgumentException("Negative probability: " + item.getProbabilityOfSample());
            }
            sum += item.getProbabilityOfSample();
            cumulated[idx++] = sum;
        }
        return cumulated;
    }
}

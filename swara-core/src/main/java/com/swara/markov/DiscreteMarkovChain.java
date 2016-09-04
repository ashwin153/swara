package com.swara.markov;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiscreteMarkovChain<T> {

    private final Map<List<T>, List<T>> transitions;
    private final Random rand;
    private List<T> seed;

    public DiscreteMarkovChain(Map<List<T>, List<T>> transitions, Random rand) {
        this.transitions = transitions;
        this.rand = rand;
    }

    @SuppressWarnings("unchecked")
    public T next() {
        // Calculate a random, valid seed state.
        while (this.seed == null || this.transitions.get(this.seed) == null) {
            final int rand = this.rand.nextInt(this.transitions.size());
            this.seed = (List<T>) this.transitions.keySet().toArray()[rand];
        }

        // Randomly select a valid next state for the given seed.
        final List<T> states = this.transitions.get(this.seed);
        final T next = states.get(this.rand.nextInt(states.size()));
        this.seed.remove(0);
        this.seed.add(next);
        return next;
    }

}

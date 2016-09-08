package com.swara.ml.markov;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

/**
 * A discrete markov chain is a unsupervised random process that undergoes transitions from one
 * state to another. Markov chains are trained on state sequences, from which they approximate the
 * state transition distribution. This transition distribution has the Markov property; the
 * probability of a state transition depends only on the 'k' previous states.
 */
public class DiscreteMarkovChain<T extends Comparable<T>> {

    private final Map<List<T>, List<T>> transitions;
    private final Random random;
    private final int k;

    public DiscreteMarkovChain(int k, Random random) {
        Preconditions.checkArgument(k > 0);
        this.transitions = new TreeMap<>();
        this.random = new Random();
        this.k = k;
    }

    public DiscreteMarkovChain(int k) {
        this(k, new Random());
    }

    /**
     * Slides a 'k' length window across the specified input and records a mapping between each 'k'
     * length state sequence (prefix) with the state that immediately follows it (suffix). Duplicate
     * suffixes for a particular prefix are permitted.
     */
    public void train(List<T> sequence) {
        for (int i = 0; i < sequence.size() - this.k; i++) {
            final List<T> prefix = sequence.subList(i, i + k);
            final T suffix = sequence.get(i + k);
            this.transitions.computeIfAbsent(prefix, k -> new ArrayList<>()).add(suffix);
        }
    }

    /**
     * Generates an infinite, but lazily calculated stream of state transitions. The iterator begins
     * at the specified seed state, or at a randomized state if the seed is null, does not exist,
     * or has no valid state transitions.
     */
    public Stream<T> generate(List<T> seed) {
        return Stream.generate(new StateIterator(seed)::next);
    }

    /**
     * An infinite, lazy iterator over the Markov state space. Every state iterator operates
     * independently, and, therefore, will produce different results. Only iterators with the same
     * seed AND the same source of randomness will produce identical results.
     */
    private final class StateIterator implements Iterator<T> {

        private List<T> state;

        public StateIterator(List<T> seed) {
            this.state = seed;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            // Calculate a random, valid current state if one does no exist.
            while (this.state == null || transitions.get(this.state) == null) {
                final int rand = random.nextInt(transitions.size());
                this.state = (List<T>) transitions.keySet().toArray()[rand];
            }

            // Randomly select a valid next state for the given seed.
            final List<T> states = transitions.get(this.state);
            final T next = states.get(random.nextInt(states.size()));
            this.state.remove(0);
            this.state.add(next);
            return next;
        }

    }

}

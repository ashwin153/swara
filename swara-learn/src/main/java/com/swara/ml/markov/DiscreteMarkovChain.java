package com.swara.ml.markov;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.swara.ml.common.Trie;

/**
 * A discrete markov chain is a unsupervised random process that undergoes transitions from one
 * state to another. Markov chains are trained on state sequences, from which they approximate the
 * state transition distribution. This transition distribution has the Markov property; the
 * probability of a state transition depends only on the 'order' previous states.
 */
public class DiscreteMarkovChain<T> implements MarkovModel<T> {

    private final Trie<T, Integer> markov;
    private final int order;

    public DiscreteMarkovChain(int order, Comparator<T> comparator) {
        Preconditions.checkArgument(order > 0);
        this.markov = new Trie<>(comparator);
        this.order = order;
    }

    @Override
    public void train(List<T> sequence) {
        // Slides an 'order' length window across the specified input sequence and records a mapping
        // between each 'order' length state sequence and the state that immediately follows it.
        IntStream.range(0, sequence.size() - this.order).parallel().forEach(i -> {
            final List<T> sub = sequence.subList(i, i + this.order + 1);
            this.markov.put(sub, v -> v == null ? 1 : ++v);
        });
    }

    @Override
    public Iterator<T> generate() {
        return new StateIterator(null);
    }

    /**
     * An infinite, lazy iterator over the Markov state space. Every state iterator operates
     * independently, and, therefore, will produce different results. Only iterators with the same
     * seed and the same source of randomness will produce identical results.
     */
    private final class StateIterator implements Iterator<T> {

        private final Random rng;
        private List<T> state;

        public StateIterator(List<T> seed) {
            this.state = seed;
            this.rng = new Random();
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            // Calculate a random, valid current state if one does no exist.
            while (this.state == null || markov.get(this.state) == null) {
                Trie<T, Integer> node = markov;
                this.state = new ArrayList<>();
                for (int i = 0; i < order; i++) {
                    final List<Trie<T, Integer>> next = node.children();
                    node = next.get(this.rng.nextInt(next.size()));
                    this.state.add(node.key());
                }
            }

            // Randomly select a valid next state for the given seed.
            final Trie<T, Integer> current = markov.get(this.state);
            final Iterator<Trie<T, Integer>> iter = current.children().iterator();

            Trie<T, Integer> next = null;
            long random = this.rng.nextInt(current.value());
            while (random >= 0 && iter.hasNext()) {
                next = iter.next();
                random -= next.value();
            }

            // Update the seed and return the selected new state.
            this.state.remove(0);
            this.state.add(next.key());
            return next.key();
        }

    }

}

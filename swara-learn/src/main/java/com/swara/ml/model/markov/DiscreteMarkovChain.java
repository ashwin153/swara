package com.swara.ml.model.markov;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.swara.ml.model.UnsupervisedModel;

/**
 *
 */
public class DiscreteMarkovChain<T> implements UnsupervisedModel<T, T> {

    private final Map<List<T>, List<T>> transitions;
    private final Random rand;
    private final int k;
    private List<T> state;

    public DiscreteMarkovChain(int k, Comparator<T> comparator) {
        Preconditions.checkArgument(k > 0);
        this.transitions =  new TreeMap<>(Ordering.from(comparator).lexicographical());
        this.rand = new Random();
        this.k = k;
    }

    @Override
    public void train(List<T> inputs) {
        this.state = null;
        for (int i = 0; i < inputs.size() - k; i++) {
            final List<T> prefix = inputs.subList(i, i + k);
            this.transitions.computeIfAbsent(prefix, k -> new ArrayList<>()).add(inputs.get(i + k));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T predict(T input) {
        // Calculate a random, valid current state if one does no exist.
        while (this.state == null || this.transitions.get(this.state) == null) {
            final int rand = this.rand.nextInt(this.transitions.size());
            this.state = (List<T>) this.transitions.keySet().toArray()[rand];
        }

        // Randomly select a valid next state for the given seed.
        final List<T> states = this.transitions.get(this.state);
        final T next = states.get(this.rand.nextInt(states.size()));
        this.state.remove(0);
        this.state.add(next);
        return next;
    }

}

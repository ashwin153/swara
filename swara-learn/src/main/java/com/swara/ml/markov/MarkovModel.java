package com.swara.ml.markov;

import java.util.Iterator;
import java.util.List;

/**
 * A markov model is used to model stochastic processes in which future state depends only on the
 * current state and not prior events. More formally, for discrete-time sequences, the Markov
 * property requires that the probability P(x_{n} | x_{n-1}, ..., x_{0}) = P(x_{n} | x_{n-1}).
 */
public interface MarkovModel<T> {

    /**
     * Markov models are trained on state sequences, from which they approximate the underlying
     * state transition probability distribution.
     */
    void train(List<T> sequence);

    /**
     * Markov models use the learned state transition probability distribution to generate an
     * arbitrary state sequence. This method returns an iterator over an infinite state sequence.
     */
    Iterator<T> generate();

}

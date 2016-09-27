package com.swara.ml.neural;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import org.apache.commons.math3.linear.RealVector;

/**
 * A artificial neural network is a supervised learning technique inspired by biological neural
 * networks that are used to approximate functions. Neural networks are trained on label vectors
 * of inputs and outputs. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 */
public class NeuralNetwork {

    private final List<NeuralLayer> layers;

    private NeuralNetwork(Builder builder) {
        this.layers = builder.layers;
    }

    /**
     *
     */
    void train(List<RealVector> inputs, List<RealVector> labels) {
        throw new UnsupportedOperationException();
    }

    /**
     * Predicts the output of the neural network for the specified input. This method is not type
     * safe, but, given that the neural network must be constructed in a type-safe way, we can
     * assume that any type errors will have already been resolved.
     */
    public RealVector predict(RealVector input) {
        RealVector result = input;
        for (NeuralLayer layer : this.layers) {
            result = layer.forward(result);
        }
        return result;
    }

    public static final class Builder {

        private final List<NeuralLayer> layers;

        public Builder() {
            this.layers = new ArrayList<>();
        }

        public Builder withLayer(NeuralLayer layer) {
            Preconditions.checkNotNull(layer);
            this.layers.add(layer);
            return this;
        }

        public Builder withNetwork(NeuralNetwork network) {
            Preconditions.checkNotNull(network);
            this.layers.addAll(network.layers);
            return this;
        }

        public NeuralNetwork build() {
            return new NeuralNetwork(this);
        }

    }
}

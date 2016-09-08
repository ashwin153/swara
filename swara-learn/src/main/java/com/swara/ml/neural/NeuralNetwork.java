package com.swara.ml.neural;

import java.util.List;
import java.util.stream.Collectors;

import com.swara.ml.neural.layer.Layer;

import org.apache.commons.math3.linear.RealVector;

/**
 * A generalized neural network trained via backpropagation through time. An artificial neural
 * network is a supervised learning technique inspired by biological neural networks that are used
 * to approximate functions. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 */
public class NeuralNetwork {

    private final double lrate;
    private final List<Layer> layers;

    public NeuralNetwork(double lrate, List<Layer> layers) {
        this.lrate = lrate;
        this.layers = layers;
    }

    /**
     *
     */
    public void train(List<RealVector> inputs, List<RealVector> outputs) {
        // Compute the predicted output for all inputs.
        final List<RealVector> predicted = inputs.stream()
            .map(this::predict)
            .collect(Collectors.toList());

        // Backpropagate errors in reverse order.
        for (int i = predicted.size() - 1; i >= 0; i--) {
            RealVector error = outputs.get(i).subtract(predicted.get(i));
            for (Layer layer : this.layers) {
                error = layer.backward(error, lrate);
            }
        }
    }

    /**
     *
     */
    public RealVector predict(RealVector input) {
        // The output of each layer becomes the input of the next layer.
        RealVector output = input;
        for (Layer layer : this.layers) {
            output = layer.forward(output);
        }

        // Return the output of the neural network.
        return output;
    }

}

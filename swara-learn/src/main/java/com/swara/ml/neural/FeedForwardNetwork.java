package com.swara.ml.neural;

import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.swara.ml.neural.layer.ForwardLayer;

import org.apache.commons.math3.linear.RealVector;

/**
 * Feed-forward networks are neural networks consisting of a sequence of {@link ForwardLayer}
 * trained via backpropagation. Backpropagation exploits the chain rule to iteratively calculate the
 * gradient of each layer in a feed-forward neural network.
 */
public class FeedForwardNetwork extends NeuralNetwork {

    private final double learnRate;

    public FeedForwardNetwork(double learnRate, ForwardLayer... layers) {
        super(layers);

        Preconditions.checkArgument(learnRate > 0);
        this.learnRate = learnRate;
    }

    @Override
    public void train(List<RealVector> inputs, List<RealVector> outputs) {
        // Pre-allocate arrays to hold action potentials and error gradients.
        final int n = this.layers().length;
        final RealVector[] activations = new RealVector[n+1];
        final RealVector[] errors = new RealVector[n+1];

        for (int x = 0; x < inputs.size() && x < outputs.size(); x++) {
            // Forward propagate output activations for each neuron in every layer. The first entry
            // activation[0] = input, to simplify the algorithm.
            activations[0] = inputs.get(x);
            for (int i = 0; i < this.layers().length; i++) {
                activations[i+1] = this.layers()[i].forward(activations[i]);
            }

            // Backpropagate error gradients in reverse order. The error of layer i is a function of
            // its output (activations[i+1]), and the error of the subsequent layer (errors[i+1]).
            errors[n] = outputs.get(n).subtract(activations[n]);
            for (int i = n-1; i >= 0; i--) {
                errors[i] = layers()[i].backward(activations[i+1], errors[i+1]);
            }

            // Update weights of each layer in parallel. While the output activations and error
            // gradients must be calculated serially because each layer is dependent on the last
            // (output of one layer becomes the input of the next), the weight updates may be
            // performed independently of layer.
            IntStream.range(0, n).parallel().forEach(i -> {
                final RealVector gradient = errors[i].mapMultiplyToSelf(this.learnRate);
                this.layers()[i].update(activations[i], gradient);
            });
        }
    }

}

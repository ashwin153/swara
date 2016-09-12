package com.swara.ml.neural;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.swara.ml.neural.layer.ForwardLayer;
import com.swara.ml.neural.layer.Layer;

import org.apache.commons.math3.linear.RealVector;

public class BackpropagationNetwork extends NeuralNetwork {

    private final double learnRate;

    public BackpropagationNetwork(List<ForwardLayer> layers, double learnRate) {
        super(layers);

        Preconditions.checkArgument(learnRate > 0);
        this.learnRate = learnRate;
    }

    @Override
    public void train(List<RealVector> inputs, List<RealVector> outputs) {
        // Pre-allocate arrays to hold action potentials and error gradients.
        final int n = this.layers().size();
        final RealVector[] activations = new RealVector[n+1];
        final RealVector[] errors = new RealVector[n+1];

        for (int x = 0; x < inputs.size() && x < outputs.size(); x++) {
            // Forward propagate output activations for each neuron in every layer. The first entry
            // activation[0] = input, to simplify the algorithm.
            activations[0] = inputs.get(x);
            for (int i = 0; i < this.layers().size(); i++) {
                activations[i+1] = this.layers().get(i).forward(activations[i]);
            }

            // Backpropagate error gradients in reverse order. The error of layer i is a function of
            // its input (activations[i]), output (activations[i+1]), and the error of the
            // subsequent layer (errors[i+1]).
            errors[n] = outputs.get(n).subtract(activations[n]);
            for (int i = n-1; i >= 0; i--) {
                errors[i] = layers().get(i).backward(activations[i], activations[i+1], errors[i+1]);
            }

            // Update weights of each layer in parallel. While the output activations and error
            // gradients must be calculated serially because each layer is dependent on the last
            // (output of one layer becomes the input of the next), the weight updates may be
            // performed independently of layer.
            IntStream.range(0, n).parallel().forEach(i -> {
                final RealVector gradient = errors[i].mapMultiplyToSelf(this.learnRate);
                this.layers().get(i).update(activations[i+1], gradient);
            });
        }
    }


//        for (int i = 0; i < inputs.size() && i < outputs.size(); i++) {
//            // Compute the predicted output for all inputs.
//            activations.push(inputs.get(i));
//            this.layers().forEach(layer -> {
//                layer.forward(activations.peek());
//            });
//              1 2 3
//            2 3 4 5
//
//            // Backpropagate errors in reverse order.
//            RealMatrix error = outputs.get(i).subtract(activations.peek());
//            for (int l = this.layers().size() - 1; l >= 0; l++) {
//                final RealVector output = activations.pop();
//                final RealVector input  = activations.peek();
//
//                RealVector gradients = this.layers().get(l).backward(input, output, error);
//
//            }
//
//            // Pop the input off the stack.
//            activations.pop();
//        }
//
//
//
//        // Backpropagate errors in reverse order.
//        for (int i = predicted.size() - 1; i >= 0; i--) {
//            RealVector error = outputs.get(i).subtract(predicted.get(i));
//            for (Layer layer : this.layers) {
//                error = layer.backward(error, lrate);
//            }
//        }
    }

}

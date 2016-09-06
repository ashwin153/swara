package com.swara.ml.model.neural;

import java.util.List;

import com.swara.ml.model.SupervisedModel;
import com.swara.ml.model.neural.layer.Layer;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public class NeuralNetwork implements SupervisedModel<RealVector, RealVector> {

    private final List<Layer> layers;

    public NeuralNetwork(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public void train(List<RealVector> inputs, List<RealVector> outputs) {
        throw new UnsupportedOperationException();
    }

    @Override
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

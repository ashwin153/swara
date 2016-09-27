package com.swara.ml.neural;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

/**
 * A artificial neural network is a supervised learning technique inspired by biological neural
 * networks that are used to approximate functions. Neural networks are trained on label vectors
 * of input s and outputs.
 *
 *
 * trained via backpropagation through time. An artificial neural
 * network is a supervised learning technique inspired by biological neural networks that are used
 * to approximate functions. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 */
public interface NeuralNetwork {


    /**
     *
     */
    void train(List<RealVector> inputs, List<RealVector> labels);

    /**
     *
     */
    RealVector predict(RealVector input);





//        // The output of each layer becomes the input of the next layer.
//        RealVector output = input;
//        for (Layer layer : this.layers) {
//            output = layer.forward(output);
//        }
//
//        // Return the output of the neural network.
//        return output;
//    }

}

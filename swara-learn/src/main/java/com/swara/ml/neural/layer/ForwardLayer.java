package com.swara.ml.neural.layer;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * A feed forward neural layer with a parameterizable activation function. Feed forward layers are
 * fully-connected; every neuron in the layer is connected to the output of every layer in the
 * previous layer. The weights of forward layers are updated via simple backpropagation.
 */
public class ForwardLayer extends Layer {

    public static final UnivariateFunction IDENTITY = i -> i;
    public static final UnivariateFunction LOGISTIC = i -> 1.0 / (1.0 + Math.exp(-i));
    public static final UnivariateFunction SOFTSIGN = i -> i / (1 + Math.abs(i));
    public static final UnivariateFunction GAUSSIAN = i -> Math.pow(Math.E, -(i * i));
    public static final UnivariateFunction STEP = i -> (i < 0) ? 0 : 1;
    public static final UnivariateFunction SINE = Math::sin;
    public static final UnivariateFunction TANH = Math::tanh;
    public static final UnivariateFunction ATAN = Math::atan;

    private final RealMatrix weights;
    private final RealVector biases;
    private final UnivariateFunction activation;

    public ForwardLayer(int inputs, int outputs, UnivariateFunction activation) {
        super(inputs, outputs);
        this.activation = activation;

        // Initialize the weights/biases of the neural network.
        final Random rand = new Random();
        final double lbound = - 1.0 / Math.sqrt(inputs);
        final double ubound = + 1.0 / Math.sqrt(inputs);

        this.biases = MatrixUtils.createRealVector(rand.doubles(outputs, lbound, ubound).toArray());
        this.weights = MatrixUtils.createRealMatrix(IntStream.range(0, outputs)
                .mapToObj(i -> rand.doubles(inputs, lbound, ubound).toArray())
                .toArray(double[][]::new)
        );
    }

    @Override
    public RealVector forward(RealVector input) {
        // Save the input and output arguments.
        this.history().push(input);
        return this.history().push(this.weights
            .operate(input)
            .add(this.biases)
            .mapToSelf(this.activation)
        );
    }

    @Override
    public RealVector backward(RealVector error, double lrate) {
        // Retrieve the output and input arguments from the history.
        final RealVector output = this.history().pop();
        final RealVector input  = this.history().pop();

        // Error for each neuron, scaled by neuron output and the learning rate.
        final RealVector delta = output
            .ebeMultiply(output.map(i -> 1 - i))
            .ebeMultiply(error)
            .mapMultiply(lrate);

        // Update the weights and biases for each neuron.
        this.biases.subtract(delta);
        this.weights.subtract(delta.outerProduct(input));

        // Propagate the error backward.
        return this.weights.preMultiply(delta);
    }

}

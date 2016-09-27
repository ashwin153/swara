package com.swara.ml.neural.layer;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * A feed forward neural layer with a parameterizable activation function. Feed forward layers are
 * fully-connected; every neuron in the layer is connected to the output of every layer in the
 * previous layer. The weights of forward layers are updated via simple backpropagation.
 */
public class ForwardLayer extends Layer {

    private final UnivariateDifferentiableFunction activation;
    private final RealMatrix weights;

    public ForwardLayer(int inputs, int outputs, UnivariateDifferentiableFunction activation) {
        super(inputs, outputs);
        this.activation = activation;

        // Initialize the weights of the layer.
        final Random rand = new Random();
        final double lbound = - 1.0 / Math.sqrt(inputs);
        final double ubound = + 1.0 / Math.sqrt(inputs);

        this.weights = MatrixUtils.createRealMatrix(IntStream.range(0, outputs + 1)
            .mapToObj(i -> rand.doubles(inputs, lbound, ubound).toArray())
            .toArray(double[][]::new)
        );
    }

    @Override
    public RealVector forward(RealVector input) {
        return this.weights.operate(input.append(1)).mapToSelf(this.activation);
    }

    @Override
    public RealVector backward(RealVector output, RealVector gradient) {
        // If it is an output neuron than it is
        derivative * (output     private final Deque<RealVector> outputs;
        - target)

        // Otherwise,
        derivative * (previousError * weights)




        final DerivativeStructure[] neurons = new DerivativeStructure[];
        for (int i = 0; i < neurons.length; i++) {

        }


        new Cos().derivative();
        .value(new DerivativeStructure(1, 1, 0, 1)).
        new Tanh().
            UnivariateDifferentiabl
        this.activation.value(new DerivativeStructure());

        return output.ebeMultiply(output.map(i -> 1 - i)).ebeMultiply(gradient);
    }

    @Override
    public void update(RealVector input, RealVector gradient, double lrate) {
        this.weights.subtract(gradient.outerProduct(input.append(1)));
    }

}

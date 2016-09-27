package com.swara.ml.neural.layers;

import java.util.Stack;

import com.swara.ml.neural.NeuralLayer;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Logistic;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * A feed forward neural layer with a parameterizable activation function. Feed forward layers are
 * fully-connected; every neuron in the layer is connected to the output of every layer in the
 * previous layer. The weights of forward layers are updated via simple backpropagation.
 */
public class ForwardLayer extends NeuralLayer {

    private final UnivariateDifferentiableFunction activation;
    private final Stack<RealVector> history;
    private RealMatrix weights;

    public ForwardLayer(int inputs, int outputs) {
        // By default, construct a sigmoid function with a 0.5 learn rate.
        this(inputs, outputs, new Logistic(1, 1, 0.5, 1, 0, 1));
    }

    public ForwardLayer(int inputs, int outputs, UnivariateDifferentiableFunction activation) {
        super(inputs, outputs);
        this.activation = activation;
        this.history = new Stack<>();
        this.weights = new Array2DRowRealMatrix(outputs, inputs + 1);
    }

    @Override
    public RealVector forward(RealVector input) {
        // Append a bias weight to the input, and calculate the linear combination.
        final RealVector netj = this.history.push(this.weights.operate(input.append(1.0)));
        return this.history.push(netj.map(this.activation));
    }

    @Override
    public RealVector backward(RealVector error) {
        final RealVector output = this.history.pop();
        final RealVector input  = this.history.pop();

        // Calculate the gradient of the activation function with respect to the last input to the
        // activation (netj in wikipedia description).
        final RealVector gradient = error.ebeMultiply(input.map(i -> this.activation
            .value(new DerivativeStructure(1, 1, 0, i))
            .getPartialDerivative(1)
        ));

        // Calculate the error that is propagated to the previous layer.
        final RealVector next = this.weights.preMultiply(gradient);
        this.weights = this.weights.add(gradient.outerProduct(output));
        return next;
    }

}

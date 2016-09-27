package com.swara.ml.neural.forward;

import java.util.Deque;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class HiddenLayer {

    private final Deque<RealVector> forward;
    private final Deque<RealVector> backward;


    private final UnivariateDifferentiableFunction activation;
    private final RealMatrix weights;

    public RealVector forward(RealVector input) {
        // Add a bias input, and calculate the weighted inputs.
        this.forward.push(this.weights.operate(input.append(1)));

        // Return result of activation function on weighted inputs.
        return this.forward.peek().map(this.activation);
    }

    public RealVector backward(RealVector gradient) {
        double[] errors = new double[];

        derivative * (previousError * weights)

    }

}

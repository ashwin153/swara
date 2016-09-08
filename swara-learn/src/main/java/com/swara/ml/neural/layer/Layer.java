package com.swara.ml.neural.layer;

import java.util.Stack;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public abstract class Layer {

    private final int inputs;
    private final int outputs;
    private final Stack<RealVector> history;

    public Layer(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.history = new Stack<>();
    }

    /**
     * Returns the number of inputs to this neural layer. The number of inputs to this layer should
     * be equal to the number of outputs of the previous layer.
     */
    public int inputs() {
        return this.inputs;
    }

    /**
     * Returns the number of outputs to this neural layer. The number of outputs to this layer
     * should be equal to the number of inputs of the next layer.
     */
    public int outputs() {
        return this.outputs;
    }

    /**
     *
     */
    public Stack<RealVector> history() {
        return this.history;
    }

    /**
     *
     */
    public abstract RealVector forward(RealVector input);

    /**
     *
     */
    public abstract RealVector backward(RealVector error, double lrate);

}

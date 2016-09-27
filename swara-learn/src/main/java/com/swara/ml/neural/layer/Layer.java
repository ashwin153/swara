package com.swara.ml.neural.layer;

import com.google.common.base.Preconditions;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public abstract class Layer {

    private final int inputs;
    private final int outputs;

    public Layer(int inputs, int outputs) {
        Preconditions.checkArgument(inputs > 0);
        Preconditions.checkArgument(outputs > 0);
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public int inputs() {
        return this.inputs;
    }

    public int outputs() {
        return this.outputs;
    }

    /**
     * Compute the output activation of each neuron.
     */
    public abstract RealVector forward(RealVector input);

    /**
     * Compute the error gradients of each input.
     */
    public abstract RealVector backward(RealVector output, RealVector error);

    /**
     * Update the weights of each neuron.
     */
    public abstract void update(RealVector input, RealVector gradient);

}

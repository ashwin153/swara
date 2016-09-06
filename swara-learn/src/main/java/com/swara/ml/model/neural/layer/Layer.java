package com.swara.ml.model.neural.layer;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public abstract class Layer {

    private final int inputs;
    private final int outputs;

    public Layer(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     *
     */
    public int inputs() {
        return this.inputs;
    }

    /**
     *
     */
    public int outputs() {
        return this.outputs;
    }

    /**
     *
     */
    public abstract RealVector forward(RealVector prev);

    /**
     *
     */
    public abstract RealVector backward(RealVector next);

}

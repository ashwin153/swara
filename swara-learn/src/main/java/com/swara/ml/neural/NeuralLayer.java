package com.swara.ml.neural;

import org.apache.commons.math3.linear.RealVector;

public abstract class NeuralLayer {

    private final int inputs;
    private final int outputs;

    public NeuralLayer(int inputs, int outputs) {
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
     *
     */
    public abstract RealVector forward(RealVector input);

    /**
     *
     */
    public abstract RealVector backward(RealVector gradient);

}

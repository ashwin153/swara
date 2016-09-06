package com.swara.ml.model.neural.layer;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public class TanhLayer extends Layer {

    private final RealMatrix weights;
    private final RealVector biases;

    public TanhLayer(int inputs, int outputs) {
        super(inputs, outputs);
        this.weights = new Array2DRowRealMatrix(outputs, inputs);
        this.biases = new ArrayRealVector(outputs);
    }

    @Override
    public RealVector forward(RealVector prev) {
        return this.weights.operate(prev).add(biases).mapToSelf(Math::tanh);
    }

    @Override
    public RealVector backward(RealVector next) {
        throw new UnsupportedOperationException();
    }

}

package com.swara.ml.kernel;

import org.apache.commons.math3.linear.RealVector;

public class SigmoidKernel implements KernelFunction {

    private final double gamma;
    private final double r;

    public SigmoidKernel(double gamma, double r) {
        this.gamma = gamma;
        this.r = r;
    }

    @Override
    public Double apply(RealVector x, RealVector y) {
        return Math.tanh(this.gamma * x.dotProduct(y) + this.r);
    }

}

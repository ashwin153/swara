package com.swara.ml.kernel;

import com.google.common.base.Preconditions;

import org.apache.commons.math3.linear.RealVector;

public class GaussianRbfKernel implements KernelFunction {

    private final double gamma;

    public GaussianRbfKernel(double gamma) {
        Preconditions.checkArgument(gamma > 0);
        this.gamma = gamma;
    }

    @Override
    public Double apply(RealVector x, RealVector y) {
        return Math.exp(-this.gamma * Math.pow(x.getDistance(y), 2));
    }

}

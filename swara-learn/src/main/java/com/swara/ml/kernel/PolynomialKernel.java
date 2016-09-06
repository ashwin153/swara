package com.swara.ml.kernel;

import com.google.common.base.Preconditions;

import org.apache.commons.math3.linear.RealVector;

public class PolynomialKernel implements KernelFunction {

    private final int d;
    private final double c;

    public PolynomialKernel(int d, double c) {
        Preconditions.checkArgument(c >= 0);
        Preconditions.checkArgument(d >= 0);
        this.d = d;
        this.c = c;
    }

    @Override
    public Double apply(RealVector x, RealVector y) {
        return Math.pow(x.dotProduct(y) + this.c, this.d);
    }

}

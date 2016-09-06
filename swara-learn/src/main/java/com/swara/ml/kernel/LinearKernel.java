package com.swara.ml.kernel;

import org.apache.commons.math3.linear.RealVector;

public class LinearKernel implements KernelFunction {

    @Override
    public Double apply(RealVector x, RealVector y) {
        return x.dotProduct(y);
    }

}

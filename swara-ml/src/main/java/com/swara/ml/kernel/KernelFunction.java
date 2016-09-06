package com.swara.ml.kernel;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
@FunctionalInterface
public interface KernelFunction {

    /**
     *
     */
    Double apply(RealVector x, RealVector y);

}
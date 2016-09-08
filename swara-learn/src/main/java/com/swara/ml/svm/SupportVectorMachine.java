package com.swara.ml.svm;

import com.swara.ml.kernel.KernelFunction;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public class SupportVectorMachine {

    private final KernelFunction kernel;

    public SupportVectorMachine(KernelFunction kernel) {
        this.kernel = kernel;
    }

    public void train(RealVector[] inputs, Integer[] outputs) {
        throw new UnsupportedOperationException();
    }

    public Integer classify(RealVector input) {
        throw new UnsupportedOperationException();
    }

}

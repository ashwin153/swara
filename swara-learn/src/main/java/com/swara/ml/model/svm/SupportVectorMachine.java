package com.swara.ml.model.svm;

import java.util.List;

import com.swara.ml.kernel.KernelFunction;
import com.swara.ml.model.SupervisedModel;

import org.apache.commons.math3.linear.RealVector;

/**
 *
 */
public class SupportVectorMachine implements SupervisedModel<RealVector, Integer> {

    private final KernelFunction kernel;

    public SupportVectorMachine(KernelFunction kernel) {
        this.kernel = kernel;
    }

    @Override
    public void train(List<RealVector> inputs, List<Integer> outputs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer predict(RealVector input) {
        throw new UnsupportedOperationException();
    }

}

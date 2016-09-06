package com.swara.ml.kernel;

import org.apache.commons.math3.linear.RealVector;

/**
 * A kernel function is a similarity function. A result of Mercer's Theorem states that under
 * certain conditions, every kernel function can be expressed as a dot product in some lifted
 * feature space. Many machine learning algorithms (like SVMs) can be expressed entirely in terms of
 * dot products, which can be replaced by kernel functions to improve the predictive performance.
 */
@FunctionalInterface
public interface KernelFunction {

    Double apply(RealVector x, RealVector y);

}
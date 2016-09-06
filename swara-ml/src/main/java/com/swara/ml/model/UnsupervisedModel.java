package com.swara.ml.model;

import java.util.List;

/**
 *
 */
public interface UnsupervisedModel<I, O> extends Model<I, O> {

    /**
     *
     */
    void train(List<I> inputs);

}

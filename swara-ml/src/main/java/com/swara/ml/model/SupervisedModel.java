package com.swara.ml.model;

import java.util.List;

/**
 *
 */
public interface SupervisedModel<I, O> extends Model<I, O> {

    /**
     *
     */
    void train(List<I> input, List<O> output);

}

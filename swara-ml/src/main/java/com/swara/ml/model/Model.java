package com.swara.ml.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public interface Model<I, O> {

    /**
     *
     */
    O predict(I input);

    @SuppressWarnings("unchecked")
    default List<O> predict(List<I> inputs) {
        return inputs.stream()
            .map(input -> predict(input))
            .collect(Collectors.toList());
    }

}

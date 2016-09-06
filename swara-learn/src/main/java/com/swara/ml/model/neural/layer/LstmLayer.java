package com.swara.ml.model.neural.layer;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * A neural layer with a long short term memory (LSTM). LSTM networks are a special class of
 * recurrent neural networks capable of learning long-term dependencies, because they are able to
 * selectively modify elements of their hidden state.
 *
 * http://colah.github.io/posts/2015-08-Understanding-LSTMs/
 */
public class LstmLayer extends Layer {

    private final SigmoidLayer forget;
    private final SigmoidLayer input;
    private final SigmoidLayer output;
    private final TanhLayer update;

    private RealVector previous;
    private RealVector memory;

    public LstmLayer(int inputs, int outputs) {
        super(inputs, outputs);
        this.forget = new SigmoidLayer(inputs + outputs, outputs);
        this.input  = new SigmoidLayer(inputs + outputs, outputs);
        this.output = new SigmoidLayer(inputs + outputs, outputs);
        this.update = new TanhLayer(inputs + outputs, outputs);
        this.previous = new ArrayRealVector(outputs);
        this.memory = new ArrayRealVector(outputs);
    }

    @Override
    public RealVector forward(RealVector prev) {
        // Concatenate the previous output and current input.
        final RealVector concat = this.previous.append(prev);

        // Update the memory.
        final RealVector last = this.forget.forward(concat).ebeMultiply(this.memory);
        final RealVector next = this.input.forward(concat).ebeMultiply(this.update.forward(concat));
        this.memory = last.add(next);

        // Calculate and return the next value.
        return this.previous = this.output.forward(concat).ebeMultiply(this.memory.map(Math::tanh));
    }

    @Override
    public RealVector backward(RealVector next) {
        throw new UnsupportedOperationException();
    }

}

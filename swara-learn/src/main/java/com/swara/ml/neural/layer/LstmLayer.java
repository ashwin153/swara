package com.swara.ml.neural.layer;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * A neural layer with a long short term memory (LSTM). LSTM networks are a special class of
 * recurrent neural networks capable of learning long-term dependencies, because they are able to
 * selectively modify elements of their hidden state.
 */
public class LstmLayer extends Layer {

    private final ForwardLayer forget;
    private final ForwardLayer input;
    private final ForwardLayer output;
    private final ForwardLayer detect;

    private RealVector memory;

    /**
     *
     *
     */
    public LstmLayer(int inputs, int outputs) {
        super(inputs, outputs);
        this.input  = new ForwardLayer(inputs + outputs, outputs, ForwardLayer.LOGISTIC);
        this.forget = new ForwardLayer(inputs + outputs, outputs, ForwardLayer.LOGISTIC);
        this.output = new ForwardLayer(inputs + outputs, outputs, ForwardLayer.LOGISTIC);
        this.detect = new ForwardLayer(inputs + outputs, outputs, ForwardLayer.TANH);

        // Initialize the memory and set the last output to empty.
        this.memory = new ArrayRealVector(outputs);
        this.history().push(this.memory);
    }

    @Override
    public RealVector forward(RealVector input) {
        // Input is a concatenation of previous output and new input.
        final RealVector in = this.history().peek().append(input);

        // Update the memory as a linear combination of recalled memory and detected updates.
        final RealVector recall = this.forget.forward(in).ebeMultiply(this.memory);
        final RealVector update = this.input.forward(in).ebeMultiply(this.detect.forward(in));
        this.memory = recall.add(update);

        // The cell selectively outputs normalized elements of its memory.
        return this.history().push(this.output.forward(in).ebeMultiply(this.memory.map(Math::tanh)));
    }

    @Override
    public RealVector backward(RealVector error, double lrate) {
        // Gradient Output Gate = error * tanh(ct) = error * norm;
        // Error cT += error * ot * (1 - tanh^2(ct))
        // Gradient Forget = err memory * last_memory
        // Gradient Input  = err memory * detect output
        // Gradient Update = err memory * err input
        // Error cT-1 = gradient * forget

//        Memory error is a combination of output error and previous memory error. Gate errors come from output or memory error and are a result of the chain rule.

        throw new UnsupportedOperationException();
    }

}

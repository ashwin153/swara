package com.swara.learn.neural.layers

/**
 * Created by ashwin on 10/20/16.
 */
class LstmLayer {

//  private final ForwardLayer forget;
//  private final ForwardLayer input;
//  private final ForwardLayer output;
//  private final ForwardLayer detect;
//  private final Stack<RealVector> history;
//  private RealVector memory;
//
//  public LstmLayer(int inputs, int outputs) {
//    super(inputs, outputs);
//    this.input  = new ForwardLayer(inputs, outputs, new Logistic(1, 1, 0.5, 1, 0, 1));
//    this.forget = new ForwardLayer(inputs, outputs, new Logistic(1, 1, 0.5, 1, 0, 1));
//    this.output = new ForwardLayer(inputs, outputs, new Logistic(1, 1, 0.5, 1, 0, 1));
//    this.detect = new ForwardLayer(inputs, outputs, new Tanh());
//
//    // Initialize the memory and set the last output to empty.
//    this.memory = new ArrayRealVector(outputs);
//    this.history = new Stack<>();
//    this.history.push(this.memory);
//  }
//
//  @Override
//  public RealVector forward(RealVector input) {
//    // Input is a concatenation of previous output and new input.
//    final RealVector in = this.history.peek().append(input);
//
//    // Update the memory as a linear combination of recalled memory and detected updates.
//    final RealVector recall = this.forget.forward(in).ebeMultiply(this.memory);
//    final RealVector update = this.input.forward(in).ebeMultiply(this.detect.forward(in));
//    this.memory = recall.add(update);
//
//    // The cell selectively outputs normalized elements of its memory.
//    return this.history.push(this.output.forward(in).ebeMultiply(this.memory.map(Math::tanh)));
//  }
//
//  @Override
//  public RealVector backward(RealVector error) {
//    // Propagate errors to various gates.
//
//    // Gradient Output Gate = error * tanh(ct) = error * norm;
//    // Error cT += error * ot * (1 - tanh^2(ct))
//    // Gradient Forget = err memory * last_memory
//    // Gradient Input  = err memory * detect output
//    // Gradient Update = err memory * err input
//    // Error cT-1 = gradient * forget
//
//    //        Memory error is a combination of output error and previous memory error. Gate errors come from output or memory error and are a result of the chain rule.
//
//    throw new UnsupportedOperationException();
//  }

}

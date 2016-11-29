package com.swara.learn.neural.layers

import breeze.numerics._

class LstmLayer(
  forget: ForwardLayer,
  input:  ForwardLayer,
  output: ForwardLayer,
  detect: ForwardLayer,
  var state:  Vector,
  var memory: Vector
) {

  def forward(x: Vector): Vector = {
    // Input is a concatenation of previous output and new input.
    val concat = Vector.vertcat(this.memory, x)

    // Update the state as a linear combination of recalled state and detected updates.
    val recall = this.forget.forward(concat) :* this.state
    val update = this.detect.forward(concat) :* this.input.forward(concat)
    this.state = recall :+ update

    // Selectively output normalized elements of its state.
    this.memory = this.output.forward(concat) :* tanh(this.state)
    this.memory
  }

  def backward(examples: Seq[(Vector, Vector, Vector)]): Seq[Vector] = {
    // Gradient Output Gate = error * tanh(ct) = error * norm;
    // Error cT += error * ot * (1 - tanh^2(ct))
    // Gradient Forget = err memory * last_memory
    // Gradient Input  = err memory * detect output
    // Gradient Update = err memory * err input
    // Error cT-1 = gradient * forget
  }

}

object LstmLayer {

  def apply(in: Int, out: Int): LstmLayer =
    new LstmLayer(
      ForwardLayer.sigmoid(in + out, out),
      ForwardLayer.sigmoid(in + out, out),
      ForwardLayer.sigmoid(in + out, out),
      ForwardLayer.tanh(in + out, out),
      Vector.zeros(out),
      Vector.zeros(out)
    )
}
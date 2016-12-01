package com.swara.learn.neural.layers

import breeze.numerics._
import com.swara.learn.neural.Result

/**
 * Special thanks to: 
 * - http://colah.github.io/posts/2015-08-Understanding-LSTMs/
 * - http://arunmallya.github.io/writeups/nn/lstm/index.html#/
 
 * @param forget Forget gate (sigmoid); "forgets" elements of the state.
 * @param input Input gate (sigmoid); determines the parts of the state that receive updates.
 * @param output Output gate (sigmoid); determines the output of the layer.
 * @param detect Detect gate (tanh); "detects" new candidate values for the state.
 */
class LstmLayer(
  forget: ForwardLayer,
  input:  ForwardLayer,
  output: ForwardLayer,
  detect: ForwardLayer,
  var state:  Vector,
  var memory: Vector
) {

  def forward(x: Vector): Result[Vector, Array[Vector]] = {
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

  def backward(examples: Seq[(Vector, Vector)]): Seq[Vector] = {
    // Output Layer Error (o_t) = error_t :* tanh(state_t)
    // State Error (c_t) += error_t :* output_t :* (1 - tanh^2(state_t))
    // Input Layer Error (i_t) = c_t :* detect_t
    // Forget Layer Error (f_t) = c_t :* state_(t-1)
    // Detect Layer Error (d_t) = c_t :* input_t
    // Previous State Error (c_(t-1)) = c_t :* forget_t
  }

//  def forward(x: Vector): Vector = {
//    // Input is a concatenation of previous output and new input.
//    val concat = Vector.vertcat(this.memory, x)
//
//    // Update the state as a linear combination of recalled state and detected updates.
//    val recall = this.forget.forward(concat) :* this.state
//    val update = this.detect.forward(concat) :* this.input.forward(concat)
//    this.state = recall :+ update
//
//    // Selectively output normalized elements of its state.
//    this.memory = this.output.forward(concat) :* tanh(this.state)
//    this.memory
//  }
//
//  def backward(examples: Seq[(Vector, Vector)]): Seq[Vector] = {
//    // Output Layer Error (o_t) = error_t :* tanh(state_t)
//    // State Error (c_t) += error_t :* output_t :* (1 - tanh^2(state_t))
//    // Input Layer Error (i_t) = c_t :* detect_t
//    // Forget Layer Error (f_t) = c_t :* state_(t-1)
//    // Detect Layer Error (d_t) = c_t :* input_t
//    // Previous State Error (c_(t-1)) = c_t :* forget_t
//  }

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

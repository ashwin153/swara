package com.swara.learn.neural.layers

import breeze.numerics._
import com.swara.learn.neural.{Layer, Result}

/**
 * A long-short term memory layer (LSTM). An LSTM is similar to a recurrent neural network, but it
 * is capable of learning long-term patterns by selectively remembering its internal state. This
 * selective memory is facilitated by a series of gates:
 *
 * - Forget: What elements of the state should be forgotten?
 * - Input:  What elements of the state should receive updates?
 * - Detect: How are elements of the state updated?
 * - Output: What elements of the state should be outputted?
 *
 * Special thanks to the following articles:
 *
 * - http://colah.github.io/posts/2015-08-Understanding-LSTMs/
 * - http://arunmallya.github.io/writeups/nn/lstm/index.html#/
 *
 * @param forget Forgets elements of the state. (sigmoid)
 * @param input Determines elements that receive updates. (sigmoid)
 * @param output Determines which elements of the state to output. (sigmoid)
 * @param detect Detects new candidate values for elements of the state. (tanh)
 */
class Lstm(
  forget: FeedForward,
  input: FeedForward,
  detect: FeedForward,
  output: FeedForward,
  private[this] var state: Vector,
  private[this] var memory: Vector
) extends Layer[Vector, Vector] {

  override def apply(inputs: Seq[Vector]): Result[Vector, Vector] = {
    // Input is a concatenation of the previous output and new input.
    inputs.foreach { x =>
      // Input is a concatenation of previous output and new input.
      val in = Vector.vertcat(x, this.memory)

      // Update the state as a linear combination of recalled state and detected updates.
      val f: Result[Vector, Vector] = this.forget(in)
      val i: Result[Vector, Vector] = this.input(in)
      val d: Result[Vector, Vector] = this.detect(in)
      val o: Result[Vector, Vector] = this.output(in)

      val recall = f.forward :* this.state
      val update = d.forward :* i.forward
      this.state = recall :+ update

      // Selectively output normalized elements of the state.
      this.memory = o.forward :* tanh(this.state)
      this.memory
    }

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

object Lstm {

  /**
   *
   * @param in
   * @param out
   * @return
   */
  def apply(in: Int, out: Int): Lstm = new Lstm(
    FeedForward.sigmoid(in + out, out),
    FeedForward.sigmoid(in + out, out),
    FeedForward.tanh(in + out, out),
    FeedForward.sigmoid(in + out, out),
    Vector.zeros(out),
    Vector.zeros(out)
  )

}

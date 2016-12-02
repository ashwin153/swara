package com.swara.learn.neural
package layers

import breeze.numerics
import breeze.optimize.DiffFunction
import breeze.stats.distributions.Rand

/**
 * A feed-forward, fully-connected layer. A feed-forward layer consists of a weight matrix, W, in
 * which W(i, j) indicates the weight of the ith neuron for the jth input, a bias vector, b, in
 * which b(k) indicates the bias of the kth neuron for all inputs, and a monotonically increasing,
 * bounded, differentiable activation function, A.
 *
 * @param weights Weight matrix.
 * @param biases Bias vector.
 * @param activation Monotonically increasing, bounded, differentiable function.
 */
class FeedForward(
  activation: DiffFunction[Double],
  weights: Matrix,
  biases: Vector
) extends Layer[Vector, Vector] {

  require(this.weights.rows == this.biases.length)

  override def apply(inputs: Seq[Vector]): Result[Vector, Vector] = {
    // Calculate the weighted output of each neuron. (Wx + b)
    val weighted = inputs.map(this.weights * _ + this.biases)

    Result(weighted.map(_.map(this.activation)), { errors =>
      (inputs, errors, weighted).zipped.map { case (in, err, out) =>
        // Calculate the gradient for each neuron as the derivative of the activation function at
        // its weighted output, multiplied by its error (Chain Rule).
        val gradient = out.map(this.activation.gradientAt) :* err

        // Update the weights/biases of the layer based on neuron gradient.
        this.biases -= gradient
        this.weights -= gradient.asDenseMatrix.t * in.asDenseMatrix

        // Backpropagate error to the previous layer.
        this.weights.t * gradient
      }
    })
  }

}

object FeedForward {

  /**
   * Constructs a forward layer that accepts the specified number of inputs and produces the
   * specified number of outputs. The initial weights and biases are uniformly random numbers
   * normalized using the equation described in http://stats.stackexchange.com/a/186351.
   *
   * @param activation Monotically increasing, bounded, differentiable function.
   * @param in Number of inputs.
   * @param out Number of outputs.
   */
  def apply(activation: DiffFunction[Double])(in: Int, out: Int): FeedForward = {
    val dist = Rand.uniform.map(x => (x * 2 - 1) / numerics.sqrt(in))
    new FeedForward(
      activation,
      Matrix.rand(out, in, dist),
      Vector.rand(out, dist)
    )
  }

  def identity: (Int, Int) => FeedForward =
    apply(new DiffFunction[Double] {
      override def calculate(x: Double): (Double, Double) = (x, 1)
    })

  def rectifier: (Int, Int) => FeedForward =
    apply(new DiffFunction[Double] {
      override def calculate(x: Double): (Double, Double) = if (x < 0) (0, 0) else (x, 1)
    })

  def sigmoid: (Int, Int) => FeedForward =
    apply(new DiffFunction[Double] {
      override def calculate(x: Double): (Double, Double) = {
        val fx = numerics.sigmoid(x)
        (fx, fx * (1 - fx))
      }
    })

  def tanh: (Int, Int) => FeedForward =
    apply(new DiffFunction[Double] {
      override def calculate(x: Double): (Double, Double) = {
        val fx = numerics.tanh(x)
        (fx, 1 - fx * fx)
      }
    })

}

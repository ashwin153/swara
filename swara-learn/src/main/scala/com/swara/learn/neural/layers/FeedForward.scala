package com.swara.learn.neural
package layers

import breeze.numerics
import breeze.stats.distributions.Rand
import com.swara.learn.neural.math._

/**
 * A feed-forward, fully-connected layer. Feed-forward layers consist of: a weight matrix (W) in
 * which W(i, j) indicates the weight of the i^th^ neuron for the j^th^ input, a bias vector (b) in
 * which b(k) indicates the bias of the k^th^ neuron for all inputs, and an activation function (f).
 * This activation function is typically non-linear and must be monotonically increasing, bounded,
 * and differentiable.
 *
 * @param activation Activation function.
 * @param weights Weight matrix.
 * @param biases Bias vector.
 */
class FeedForward(
  activation: Activation,
  weights: Matrix,
  biases: Vector
) extends Layer[Vector, Vector] {

  /**
   * Returns the result of applying the feed-forward layer to the specified input. Feed-forward
   * layers simply take the linear combination Wx + b and apply the activation function to the
   * result. Training a feed-forward layer is equally straightforward; because the activation
   * function is monotonically increasing and differentiable, we can backpropagate error using
   * gradient descent.
   *
   * @param inputs Input vectors.
   * @return Result of applying the layer.
   */
  override def apply(inputs: Seq[Vector]): Result[Seq[Vector], Seq[Vector]] = {
    val weighted = inputs.map(this.weights * _ + this.biases)
    val activate = weighted.map(this.activation)

    Result(activate, { errors: Seq[Vector] =>
      (inputs, errors, weighted).zipped.map { case (x, err, net) =>
        val gradient  = this.activation.gradient(net) :* err
        val propagate = this.weights.t * gradient
        this.biases  -= gradient
        this.weights -= gradient.asDenseMatrix.t * x.asDenseMatrix
        propagate
      }
    })
  }

}

object FeedForward {

  /**
   * Constructs a forward layer that accepts the specified number of inputs and produces the
   * specified number of outputs. The initial weights and biases are random numbers selected from
   * the specified distribution. By default, the initial distribution is equivalent to the one
   * described here: http://stats.stackexchange.com/a/186351.
   *
   * @param activation Activation function.
   * @param inputs Number of inputs.
   * @param outputs Number of outputs.
   * @param init Initial distribution of weights and biases.
   */
  def apply(
    activation: Activation,
    inputs: Int,
    outputs: Int
  )(
    init: Rand[Double] = Rand.uniform.map(x => (x * 2 - 1) / numerics.sqrt(inputs))
  ): FeedForward = new FeedForward(
    activation,
    Matrix.rand(outputs, inputs, init),
    Vector.rand(outputs, init)
  )

}

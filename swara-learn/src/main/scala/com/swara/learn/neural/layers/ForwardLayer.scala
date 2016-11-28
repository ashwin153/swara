package com.swara.learn.neural.layers

import breeze.linalg._
import breeze.numerics._
import breeze.optimize.DiffFunction
import breeze.stats.distributions.Rand

/**
 * A feed-forward, fully-connected layer. A forward layer consists of a weight matrix, W, in which
 * W(i, j) indicates the weight of the ith neuron for the jth input, a bias vector, B, in which B(k)
 * indicates the bias of the kth neuron for all inputs, and a monotonically increasing, bounded,
 * differentiable activation function, A. The forward layer is essentially a linear combination
 * machine; for some input vector x, the output of the forward layer is A(Wx + B).
 *
 * @param activation Monotonically increasing, bounded, differentiable function.
 * @param weights Weight matrix.
 * @param biases Bias vector.
 */
class ForwardLayer(activation: DiffFunction[Double], weights: Matrix, biases: Vector) {

  require(this.weights.rows == this.biases.length)

  def forward(input: Vector): Vector =
    (this.weights * input + this.biases).map(activation)

  def backward(examples: Seq[(Vector, Vector, Vector)]): Seq[Vector] = {
    // Calculate the gradients for each neuron, weight updates, and propagated error.
    val (gradient, updates, propagated) = examples.map { case (input, _, error) =>
      val gradient = (this.weights * input + this.biases).map(activation.gradientAt) :* error
      val updates = gradient.asDenseMatrix * input.toDenseMatrix.t
      val propagated = this.weights.t * gradient
      (gradient, updates, propagated)
    }.unzip3

    // Update biases and weights using the calculated neuron gradients and weight updates.
    this.biases -= gradient
    this.weights -= updates

    // Propagate the calculated error to the previous layer.
    propagated
  }
}

object ForwardLayer {

  lazy val Identity = new DiffFunction[Double] {
    override def calculate(x: Double): (Double, Double) = (x, 1)
  }

  lazy val Sigmoid = new DiffFunction[Double] {
    override def calculate(x: Double): (Double, Double) = {
      val fx = sigmoid(x)
      (fx, fx * (1 - fx))
    }
  }

  lazy val Tanh = new DiffFunction[Double] {
    override def calculate(x: Double): (Double, Double) = {
      val fx = tanh(x)
      (fx, 1 - fx * fx)
    }
  }

  lazy val ReLU = new DiffFunction[Double] {
    override def calculate(x: Double): (Double, Double) = if (x < 0) (0, 0) else (x, 1)
  }

  /**
   * Constructs a forward layer that accepts the specified number of inputs and produces the
   * specified number of outputs. The initial weights and biases are uniformly random numbers
   * normalized using the equation described in http://stats.stackexchange.com/a/186351.
   *
   * @param activation Monotically increasing, bounded, differentiable function.
   * @param in Number of inputs.
   * @param out Number of outputs.
   */
  def apply(activation: DiffFunction[Double], in: Int, out: Int): ForwardLayer =
    new ForwardLayer(
      activation,
      DenseMatrix.rand(out, in, Rand.uniform.map(_ * 2 - 1).map(_ * sqrt(6.0 / (in + out)))),
      DenseVector.zeros(out)
    )

}

package com.swara.learn.neural.math

/**
 * An activation function. Activation functions are monotonically increasing, differentiable, and
 * bounded and are typically non-linear.
 */
trait Activation extends (Vector => Vector) {

  def gradient(x: Vector): Vector

}

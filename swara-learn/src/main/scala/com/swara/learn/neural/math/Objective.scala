package com.swara.learn.neural.math

/**
 *
 */
trait Objective extends ((Vector, Vector) => Double) {

  def loss(actual: Vector, expected: Vector): Vector

}

package com.swara.learn.neural.math

/**
 *
 */
trait Objective {

  def apply(actual: Vector, expected: Vector): Double

  def loss(actual: Vector, expected: Vector): Vector

}

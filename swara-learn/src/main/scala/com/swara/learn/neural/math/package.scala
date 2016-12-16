package com.swara.learn.neural

import breeze.numerics._

package object math {

  type Vector = breeze.linalg.DenseVector[Double]
  val Vector: breeze.linalg.DenseVector.type = breeze.linalg.DenseVector
  type Matrix = breeze.linalg.DenseMatrix[Double]
  val Matrix: breeze.linalg.DenseMatrix.type = breeze.linalg.DenseMatrix

  object Identity extends Activation {
    override def apply(x: Vector): Vector = x
    override def gradient(x: Vector): Vector = Vector.ones(x.length)
  }

  object Rectifier extends Activation {
    override def apply(x: Vector): Vector = x.map(i => if (i < 0) 0.0 else i)
    override def gradient(x: Vector): Vector = x.map(i => if (i < 0) 0.0 else 1.0)
  }

  object Sigmoid extends Activation {
    override def apply(x: Vector): Vector = sigmoid(x)
    override def gradient(x: Vector): Vector = sigmoid(x) * (1 - sigmoid(x))
  }

  object Tanh extends Activation {
    override def apply(x: Vector): Vector = tanh(x)
    override def gradient(x: Vector): Vector = 1.0 - (tanh(x) :^ 2.0)
  }


}

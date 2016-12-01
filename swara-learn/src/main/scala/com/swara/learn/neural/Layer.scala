package com.swara.learn.neural

trait Layer[I, O] {

  /**
   * A result represents the outcome of the application of a [[Layer]] to a particular input.
   * Results contain a 'forward' attribute, which is the value that is passed forward as input to
   * the next layer in a network, and a 'backward' attribute, which is a backpropagation function
   * that calculates the error to pass backward to the previous layer in a network.
   *
   * @param forward Forward result.
   * @param backward Backpropagation function.
   */
  case class Result(forward: Seq[O], backward: Seq[O] => Seq[I])

  case class Cons[A, B, C](prev: Layer[A, B], next: Layer[B, C]) extends Layer[A, C] {
    override def apply(x: Seq[A]): Result = {
      val r1 = prev(x)
      val r2 = next(r1.forward)
      Result(r2.forward, r2.backward andThen r1.backward)
    }
  }

  def apply(inputs: Seq[I]): Result

  def ::[T](that: Layer[O, T]): Layer[I, T] = Cons(this, that)

}

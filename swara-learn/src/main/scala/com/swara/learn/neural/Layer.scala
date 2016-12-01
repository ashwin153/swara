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

  def apply(inputs: Seq[I]): Result

}

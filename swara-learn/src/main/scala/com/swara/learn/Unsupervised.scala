package com.swara.learn

/**
  * An unsupervised model is training against a sequence of ''unlabeled'' examples. Each examples
  * consists only of an input; therefore, there is way to evaluate a potential solution. Examples of
  * unsupervised models include K-Means and Discrete Markov Chains.
  *
  * @tparam I Type of inputs.
  */
trait Unsupervised[I] {

  def train(input: I)

}

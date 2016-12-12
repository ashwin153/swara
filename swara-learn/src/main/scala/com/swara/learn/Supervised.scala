package com.swara.learn

/**
 * A supervised model is trained against a sequence of labeled examples. Each example consists of
 * an input and an expected output; this information is consumed by the model and used to infer
 * a function that may be used to map new examples. Examples of supervised models include Support
 * Vector Machines and Neural Networks.
 *
 * @tparam I Type of inputs.
 * @tparam O Type of outputs.
 */
trait Supervised[I, O] {

  def train(input: I, output: O)

}

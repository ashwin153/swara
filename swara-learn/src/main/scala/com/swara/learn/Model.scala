package com.swara.learn

/**
 * A predictive model utilizes various machine learning and statistical techniques to predict the
 * most likely outcomes for a specified input. The purpose of this library is to implement generic
 * predictive models that may be applied to a variety of prediction problems.
 *
 * @tparam I Type of inputs.
 * @tparam O Type of outputs.
 */
trait Model[I, O] {

  def predict(input: I): O

}

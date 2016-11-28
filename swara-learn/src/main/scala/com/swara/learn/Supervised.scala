package com.swara.learn

/**
 * Supervised learning techniques build a model from a set of label training examples. Examples of
 * supervised methods include Support Vector Machines and Neural Networks.
 */
trait Supervised[I, O] {

  def train(data: Seq[(I, O)])

}

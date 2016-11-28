package com.swara.learn

/**
 * Unsupervised learning techniques build a model from a set of training examples. Examples of
 * unsupervised methods include Markov Models and K-Means.
 */
trait Unsupervised[I, O] extends Model[I, O] {

  def train(inputs: Seq[I])

}

package com.swara.learn

/**
 * Predictive models utilize machine learning and statistical methods to predict the outcome for
 * a particular set of inputs. In a sense, predictive models are extrapolation engines.
 */
trait Model[I, O] {

  def predict(input: I): O

}
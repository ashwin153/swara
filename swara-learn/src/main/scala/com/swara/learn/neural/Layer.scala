package com.swara.learn.neural

import breeze.linalg.Tensor

trait Layer[I, O] {

  /**
   * Applies the layer to the specified sequence of inputs. Returns a tuple containing the 
   * outputs of the layer as well as a backpropagation function that takes a sequence of
   * output errors, calculates and applies weight updates, and returns a sequence of input 
   * errors.
   */
  def apply(x: Seq[I]): (Seq[O], Seq[O] => Seq[I])

}

package com.swara.learn.neural.layers

import com.swara.learn.neural.{Layer, Result}

/**
 *
 * @tparam T
 */
class Identity[T] extends Layer[T, T] {

  override def apply(inputs: Seq[T]): Result[T, T] = Result(inputs, identity)

}

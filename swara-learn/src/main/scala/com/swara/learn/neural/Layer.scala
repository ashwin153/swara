package com.swara.learn.neural

/**
 * A layer in a neural network. An artificial neural network can be thought of as a "layered"
 * computation; inputs flow forward through the network from layer to layer to produce an output,
 * and errors flow backward to train each layer to produce more accurate results.
 *
 * @tparam I Type of input.
 * @tparam O Type of output.
 */
trait Layer[-I, +O]  {

  /**
   * Returns a [[Result]] containing the outcome of applying the layer to the specified sequence of
   * inputs.
   *
   * @param x Sequence of inputs.
   * @return Result of applying the layer.
   */
  def apply(x: Seq[I]): Result[Seq[I], Seq[O]]

  /**
   * Concatenates this layer and the specified layer. Performs the equivalent of functional
   * composition. Concatenation enables layers to be chained together in a type-safe way to form
   * arbitrarily complex neural networks.
   *
   * @param that Next layer.
   * @tparam T Type of output.
   * @return Concatenated layers.
   */
  def ::[T](that: Layer[O, T]): Layer[I, T] = new Layer[I, T] {
    override def apply(x: Seq[I]): Result[Seq[I], Seq[T]] = {
      val r1 = Layer.this(x)
      val r2 = that(r1.forward)
      Result(r2.forward, r2.backward andThen r1.backward)
    }
  }

}

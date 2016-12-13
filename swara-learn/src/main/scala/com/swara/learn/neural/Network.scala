package com.swara.learn.neural

import com.swara.learn.{Model, Supervised}

/**
 * An artificial neural network. A neural network is a supervised learning technique inspired by
 * biological neural networks that are used to approximate functions. Implementation provides a
 * flexible and extensible way to build arbitrarily complex networks (rnn, cnn, etc.).
 *
 * @param loss Loss function s.t. (actual, expected) => loss
 * @param layer Layers that form the network.
 * @tparam I Type of inputs.
 * @tparam O Type of outputs.
 */
class Network[I, O](
  loss: ((O, O) => O),
  layer: Layer[I, O]
) extends Model[Seq[I], Seq[O]] with Supervised[Seq[I], Seq[O]] {

  override def train(inputs: Seq[I], outputs: Seq[O]): Unit = {
    val result = this.layer(inputs)
    val errors = result.forward.zip(outputs).map(this.loss.tupled)
    result.backward(errors)
  }

  override def predict(input: Seq[I]): Seq[O] = {
    this.layer(input).forward
  }

}

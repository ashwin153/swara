package com.swara.learn.neural

import com.swara.learn.{Model, Supervised}

/**
 * An artificial neural network is a supervised learning technique inspired by biological neural
 * networks that are used to approximate functions. Neural networks are trained on labeled vectors
 * of inputs and outputs. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 *
 * @param loss Loss function.
 * @param layer Layer(s) that form the network.
 * @tparam I Type of inputs.
 * @tparam O Type of outputs.
 */
class Network[I, O](
  loss: ((O, O) => O),
  layer: Layer[I, O]
) extends Model[Seq[I], Seq[O]] with Supervised[I, O] {

  override def train(examples: Seq[(I, O)]): Unit = {
    val (inputs, expected) = examples.unzip
    val result = this.layer(inputs)
    val errors = result.forward.zip(expected).map(this.loss.tupled)
    result.backward(errors)
  }

  override def predict(input: Seq[I]): Seq[O] = {
    this.layer(input).forward
  }

}
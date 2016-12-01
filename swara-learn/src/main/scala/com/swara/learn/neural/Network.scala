package com.swara.learn.neural

/**
 * An artificial neural network is a supervised learning technique inspired by biological neural
 * networks that are used to approximate functions. Neural networks are trained on labeled vectors
 * of inputs and outputs. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 */
class Network[I, O](layer: Layer[I, O], loss: ((O, O) => O)) {

  def train(data: Seq[(I, O)]): Unit = {
    val (inputs, expected) = data.unzip
    val result = this.layer(inputs)
    val errors = result.forward.zip(expected).map(this.loss.tupled)
    result.backward(errors)
  }

  def predict(inputs: I): O = this.layer(Seq(inputs)).forward.head

}
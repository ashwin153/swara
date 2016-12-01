package com.swara.learn.neural

/**
 * A artificial neural network is a supervised learning technique inspired by biological neural
 * networks that are used to approximate functions. Neural networks are trained on label vectors
 * of inputs and outputs. This implementation provides a flexible and extensible model for
 * building arbitrarily complex networks (rnn, cnn, etc.).
 */
class Network[I, O](layer: Layer[I, O]) {

  def train(data: List[(I, O)]): Unit = ???

  def predict(input: I): O = ???

}

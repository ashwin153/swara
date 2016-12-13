package com.swara.learn.neural

/**
 * A result represents the outcome of the application of a [[Layer]] to a particular input.
 * Results contain a 'forward' attribute, which is the value that is passed forward as input to
 * the next layer in a network, and a 'backward' attribute, which is a backpropagation function
 * that calculates the error to pass backward to the previous layer in a network.
 *
 * @param forward Forward value.
 * @param backward Backpropagation function.
 */
case class Result[-I, +O](forward: O, backward: O => I)
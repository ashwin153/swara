package com.swara.examples.learn

import com.swara.learn.markov.DiscreteMarkovChain

/**
 * A random writer generates a string of words via a discrete markov chain. Random writers learn the
 * probability distribution of each word given the previous k words and then use this distribution
 * to generate a sequence of words beginning from a randomly selected seed.
 */
object RandomTextExample {

  def apply(source: String): String = {
    val markov = DiscreteMarkovChain.empty[String](4)
    markov.train(source.toLowerCase().split("\\W+").toSeq)
    markov.generate().take(100).foldRight("")((word, rest) => word + " " + rest)
  }

}

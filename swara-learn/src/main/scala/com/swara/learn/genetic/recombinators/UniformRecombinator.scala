package com.swara.learn.genetic.recombinators

import com.swara.learn.genetic.Recombinator
import scala.util.Random

/**
 * A uniform recombinator. Uniform recombinators iteratively generates a child by randomly selecting
 * from each parent for each gene. Therefore, the offspring will have approximately half of its
 * genes from the father and half from the mother. The ratio of expected genes from the father and
 * mother can be tuned via the bias parameter.
 *
 * @param bias Expected percentage of genes from the father.
 * @tparam T Type of genome.
 */
class UniformRecombinator[T](bias: Double = 0.50) extends Recombinator[Seq[T]] {

  require(bias >= 0.0 && bias < 1.0, "Bias must be on the interval [0, 1].")

  override def crossover(father: Seq[T], mother: Seq[T]): Seq[T] =
    father.zip(mother).map(if (Random.nextDouble() < bias) _._1 else _._2)

}
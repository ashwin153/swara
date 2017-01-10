package com.swara.learn.genetic.recombinators

import com.swara.learn.genetic.Recombinator
import scala.util.Random

/**
 * A uniform recombinator.
 *
 * https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)#Uniform_crossover_and_half_uniform_crossover
 *
 * @tparam T Type of genome.
 */
class UniformRecombinator[T] extends Recombinator[Seq[T]] {

  override def crossover(father: Seq[T], mother: Seq[T]): Seq[T] = {
    require(father.size == mother.size, "Parent genomes must be of equal length.")
    father.zip(mother).map(if (Random.nextBoolean()) _._1 else _._2)
  }

}
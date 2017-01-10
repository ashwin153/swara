package com.swara.learn.genetic

import net.jcip.annotations.ThreadSafe

/**
 * A recombination operator. Recombinators crossover the genetic makeup of two parent individuals
 * in order to produce offspring that are representative of both parent genomes.
 *
 * @tparam T Type of genome.
 */
@ThreadSafe
trait Recombinator[T] {

  def crossover(father: T, mother: T): T

}

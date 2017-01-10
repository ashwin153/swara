package com.swara.learn.genetic
package mutators

import scala.util.Random

/**
 * A single point mutator. Single point mutation calls for generating a random value for each gene
 * in the genome which indicates whether or not the gene will be mutated. Single point mutation is
 * based on biological point mutation.
 *
 * @param rate Mutation rate.
 * @param mutate Gene mutator.
 * @tparam T Type of genome.
 */
class SinglePointMutator[T](rate: Double, mutate: T => T) extends Mutator[Seq[T]] {

  require(rate >= 0.0 && rate < 1.0, "Rate must be on the interval [0.0, 1.0].")

  override def mutate(genome: Seq[T]): Seq[T] =
    genome.map(gene => if (Random.nextDouble() < this.rate) gene else this.mutate(gene))

}
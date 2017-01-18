package com.swara.learn.genetic.mutators

import com.swara.learn.genetic.Mutator
import scala.util.Random

/**
 *
 * @param rate
 * @tparam T Type of genome.
 */
class SwapMutator[T](rate: Double) extends Mutator[Seq[T]] {

  require(rate >= 0.0 && rate < 1.0, "Rate must be on the interval [0.0, 1.0].")

  override def mutate(genome: Seq[T]): Seq[T] =
    if (Random.nextDouble() < this.rate) {
      val r1 = Random.nextInt(genome.size)
      val r2 = Random.nextInt(genome.size)
      genome.updated(r2, genome(r1)).updated(r1, genome(r2))
    } else {
      genome
    }

}

package com.swara.learn.genetic

import net.jcip.annotations.ThreadSafe

/**
 * A mutation operation. Mutators mutate the genetic makeup of individuals in order to introduce new
 * genetic information into a population and, thereby, increase genetic variability.
 *
 * @tparam T Type of genome.
 */
@ThreadSafe
trait Mutator[T] {

  def mutate(genome: T): T

}

package com.swara.learn.genetic

import net.jcip.annotations.ThreadSafe

/**
 * A fitness evaluator. Evolutionary fitness numerically encodes the rules of Darwinian natural
 * selection; individuals with greater fitness are more likely to produce offspring relative to its
 * peers. Fitness must be a non-negative real number.
 *
 * @tparam T Type of genome.
 */
@ThreadSafe
trait Evaluator[T] {

  def fitness(genome: T): Double

}

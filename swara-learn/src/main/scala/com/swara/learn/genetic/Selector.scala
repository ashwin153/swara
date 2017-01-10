package com.swara.learn.genetic

import net.jcip.annotations.ThreadSafe

/**
 * A selection operator. Selectors determine which individuals in a population will be selected for
 * recombination.
 *
 * @tparam T Type of genome.
 */
@ThreadSafe
trait Selector[T] {

  def select(individuals: Seq[Individual[T]], n: Int): Seq[Individual[T]]

}

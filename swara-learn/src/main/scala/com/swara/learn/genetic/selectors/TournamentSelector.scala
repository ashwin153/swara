package com.swara.learn.genetic
package selectors

import scala.util.Random

/**
 *
 * @tparam T Type of genome.
 */
class TournamentSelector[T](size: Int) extends Selector[T] {

  override def select(individuals: Seq[Individual[T]], n: Int): Seq[Individual[T]] =
    Seq.fill(n)(Random.shuffle(individuals).take(size).maxBy(_.fitness))

}

package com.swara.learn.genetic
package selectors

import scala.collection.immutable.TreeMap
import scala.util.Random

/**
 * A roulette wheel selector. Roulette wheel selection is the process of randomly selecting
 * individuals in proportion to their fitness.
 *
 * @tparam T Type of genome.
 */
class RouletteWheelSelector[T] extends Selector[T] {

  override def select(individuals: Seq[Individual[T]], n: Int): Seq[Individual[T]] = {
    // Build a prefix-sum of individual fitness and store in a tree map. This allows us to perform
    // fitness-proportionate random selection in O(|I| log |I| + n log |I|).
    var wheel = TreeMap.empty[Double, Individual[T]]
    var total = 0.0

    individuals.foreach { i =>
      total += i.fitness
      wheel += total -> i
    }

    // Randomly sample from the roulette wheel to choose the selected individuals.
    Seq.fill(n)(wheel.valuesIteratorFrom(Random.nextDouble() * total).next())
  }

}

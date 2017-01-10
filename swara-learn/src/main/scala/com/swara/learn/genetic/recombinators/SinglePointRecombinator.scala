package com.swara.learn.genetic
package recombinators

import scala.util.Random

/**
 * A single point recombinator. Single point recombination calls for a single point to be selected
 * in the parent genome sequences. All genes before the point are taken from one parent and all
 * genes after that point are taken from the other. For example, for some father f1, f2, f3, f4 and
 * mother m1, m2, m3, m4 and point 3, the child genome is f1, f2, m3, f4.
 *
 * @tparam T Type of genome.
 */
class SinglePointRecombinator[T] extends Recombinator[Seq[T]] {

  override def crossover(father: Seq[T], mother: Seq[T]): Seq[T] = {
    val point = Random.nextInt(father.size min mother.size)
    father.take(point) ++ mother.drop(point)
  }

}

package com.swara.learn.genetic
package recombinators

import scala.util.Random

/**
 * A two point recombinator. Two-point recombination calls for two points to be selected in the
 * parent genome sequences. Everything between the two points is swapped between the parent genomes
 * in order to produce a child genome. For example, for some father f1, f2, f3, f4 and mother
 * m1, m2, m3, m4 and points 1 and 3, the child genome is f1, m2, m3, f4.
 *
 * @tparam T Type of genome.
 */
class TwoPointRecombinator[T] extends Recombinator[Seq[T]] {

  override def crossover(father: Seq[T], mother: Seq[T]): Seq[T] = {
    val p1 = Random.nextInt(father.size min mother.size)
    val p2 = Random.nextInt(father.size min mother.size)
    father.take(p1) ++ mother.slice(p1, p2) ++ father.drop(p2)
  }

}
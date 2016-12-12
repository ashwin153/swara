package com.swara.learn.common

import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
 * A probability distribution. Implementation is a simple wrapper around a Guava [[Multiset]], with
 * Scala collection semantics. Provides a convenient interface for taking weighted random samples
 * and determining the probabilities of elements within a set. Distributions are thread-safe iff the
 * underlying multiset is thread-safe.
 *
 * @tparam X Type of elements.
 */
class Distribution[X](elements: Multiset[X]) extends mutable.Set[X] {

  /**
   * Returns the probability with which the specified element 'x' occurs in the distribution.
   * Probabilities are guaranteed to be on the interval [0, 1].
   *
   * @param x Element in distribution.
   * @return Probability of element.
   */
  def probability(x: X): Double =
    if (this.elements.isEmpty) 0.0 else this.elements.count(x) / this.elements.size().toDouble

  /**
   * Returns a probability-weighted random element from the distribution.
   *
   * @return Weighted random element.
   */
  def sample(): X = {
    var rand = Random.nextInt(this.elements.size())
    this.iterator.dropWhile { x =>
      rand -= this.elements.count(x)
      rand >= 0
    }.next
  }

  override def +=(x: X): Distribution[X] = {
    this.elements.add(x)
    this
  }

  override def -=(x: X): Distribution[X] = {
    this.elements.remove(x)
    this
  }

  override def contains(x: X): Boolean = this.elements.contains(x)

  override def iterator: Iterator[X] = this.elements.iterator().asScala
  
}

object Distribution {

  /**
   * Constructs an empty distribution backed by a Guava [[ConcurrentHashMultiset]]. Because the
   * underlying collection is thread-safe, the constructed distribution is also thread-safe.
   *
   * @tparam X Type of elements.
   * @return An empty distribution.
   */
  def empty[X]: Distribution[X] = new Distribution[X](ConcurrentHashMultiset.create())

}
package com.swara.learn.common

import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import net.jcip.annotations.NotThreadSafe
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
 * A discrete probability distribution. Implementation is a simple wrapper around a Guava
 * [[Multiset]], with Scala collection semantics. Provides a convenient interface for taking
 * weighted random samples and determining the probabilities of elements within a set. A
 * distribution may be queried in parallel and modified in parallel, but not concurrently queried
 * and modified.
 *
 * @param elements Initial elements.
 * @tparam X Type of elements.
 */
@NotThreadSafe
class Distribution[X](elements: Multiset[X]) extends mutable.Set[X] {

  override def +=(x: X): Distribution[X] = {
    this.elements.add(x)
    this
  }

  override def -=(x: X): Distribution[X] = {
    this.elements.remove(x)
    this
  }

  override def contains(x: X): Boolean =
    this.elements.contains(x)

  override def iterator: Iterator[X] =
    this.elements.iterator().asScala

  /**
   * Returns the probability with which the specified element occurs in the distribution. Calculated
   * as the frequency of the element divided by total number of elements in the distribution. O(1)
   *
   * @param x Element in distribution.
   * @return Probability of element.
   */
  def probability(x: X): Double =
    if (this.elements.isEmpty) 0.0 else this.elements.count(x) / this.elements.size().toDouble

  /**
   * Returns a probability-weighted random element from the distribution. Selects a uniformly random
   * number less than the total number of elements in the distribution, and subtract the frequency
   * of each element in the distribution until the number is negative. O(n)
   *
   * @throws NoSuchElementException If the distribution is empty.
   * @return Weighted random element.
   */
  def sample(): X = {
    var rand = Random.nextInt(this.elements.size())
    this.iterator.dropWhile { x =>
      rand -= this.elements.count(x)
      rand >= 0
    }.next
  }

}

object Distribution {

  /**
   * Constructs an empty distribution backed by a Guava [[ConcurrentHashMultiset]].
   *
   * @tparam X Type of elements.
   * @return An empty distribution.
   */
  def empty[X]: Distribution[X] =
    new Distribution[X](ConcurrentHashMultiset.create())

  /**
   * Constructs a distribution backed by a Guava [[ConcurrentHashMultiset]] and initializes it with
   * the specified iterable collection of elements.
   *
   * @param iterable Initial elements.
   * @tparam X Type of elements.
   * @return A distribution with the specified initial elements.
   */
  def apply[X](iterable: Iterable[X]): Distribution[X] =
    new Distribution[X](ConcurrentHashMultiset.create(iterable.asJava))

}
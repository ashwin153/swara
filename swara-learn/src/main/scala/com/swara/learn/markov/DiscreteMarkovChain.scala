package com.swara.learn.markov

import com.swara.learn.{Model, Unsupervised}
import com.swara.learn.common.Trie
import net.jcip.annotations.ThreadSafe
import scala.util.Random

/**
 * A discrete markov chain is a unsupervised random process that undergoes transitions from one
 * state to another. Discrete markov chains are a special case of a family of stochastic models
 * known as markov models, which are used to model random processes in which future states depend
 * only on the current state and not on any prior events. More formally, for discrete sequences,
 * the Markov property requires that the P(x_{n} | x_{n-1}, ... , x_{0}) = P(x_{n} | x_{n-1}).
 * Markov chains are trained on state sequences, from which they approximate the aforementioned
 * state transition probability distribution.
 *
 * @tparam T Type of states.
 */
@ThreadSafe
class DiscreteMarkovChain[T](order: Int) extends Model[Seq[T], T] with Unsupervised[Seq[T]] {

  private[this] val transitions = Trie.empty[T, Int]

  /**
   * Markov models are trained on state sequences, from which they approximate the underlying
   * state transition probability distribution. Implementation slides an 'order + 1' length window
   * across the specified state sequence in parallel and records a mapping between each 'order'
   * length state sequence and the state that immediately follows it.
   *
   * @param example Sequence of states to train on.
   */
  override def train(example: Seq[T]): Unit =
    example.par.iterator
      .sliding(this.order + 1, 1)
      .foreach(seq => this.transitions.put(seq.toList, (suffix, prev) => (suffix, prev) match {
        case (_, None) => Some(1)
        case (_, Some(value)) => Some(value + 1)
      }))

  /**
   * Returns the next state. The next state is chosen using the underlying transition probabilities
   * that define the model. Markov models are inherently non-deterministic; however, they rely on
   * structured randomness to select the most likely next state for any given state sequence. Throws
   * a Runtime Exception if the markov chain has not yet been trained, because all models must be
   * trained before they may be used for predictive tasks.
   *
   * @param states Sequence of states.
   * @return Random next state.
   */
  override def predict(states: Seq[T]): T = {
    val cur = this.transitions.closest(states.toList)
    var num = Random.nextInt(cur.value.getOrElse(0))
    cur.children.dropWhile { child =>
      num -= child.value.getOrElse(0)
      num >= 0
    }.next.symbol.get
  }
  
  /**
   * Markov chains can use the learned state transition probability distribution to generate an
   * arbitrary state sequence. Returns an infinite iterator; note, an iterator is preferable to a
   * stream because streams store all previously computed values in memory.
   *
   * @return An infinite iterator over states.
   */
  def generate(seed: Seq[T] = Seq.empty): Iterator[T] = {
    val state = seed.take(this.order).toBuffer
    Iterator.continually {
      val next = predict(state.toList)
      state.takeRight(this.order - 1) :+ next
      next
    }
  }

}

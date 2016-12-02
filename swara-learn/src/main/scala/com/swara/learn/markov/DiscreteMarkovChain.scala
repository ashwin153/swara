package com.swara.learn.markov

import com.swara.learn.common.Trie
import scala.collection.mutable.ListBuffer
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
class DiscreteMarkovChain[T](order: Int) {

  private[this] val transitions = Trie[T, Int]

  /**
   * Markov models are trained on state sequences, from which they approximate the underlying
   * state transition probability distribution. Implementation slides an 'order + 1' length window
   * across the specified state sequence in parallel and records a mapping between each 'order'
   * length state sequence and the state that immediately follows it.
   *
   * @param states Sequence of states to train on.
   */
  def train(states: Seq[T]): Unit = {
    states.par.iterator.sliding(this.order + 1, 1).foreach(seq =>
      this.transitions.put(seq.toList, (suffix, prev) => (suffix, prev) match {
        case (_, None) => Some(1)
        case (_, Some(value)) => Some(value + 1)
      })
    )
  }

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
  def predict(states: Seq[T]): T = {
    val cur = this.transitions.get(states.toList)
    var num = Random.nextInt(cur.value.getOrElse(0))

    val iter = cur.children.iterator.dropWhile(child => {
      num -= child.value.getOrElse(0)
      num > 0
    })

    if (iter.hasNext) iter.next.symbol.get else cur.symbol.get
  }

  /**
   * Markov chains can use the learned state transition probability distribution to generate an
   * arbitrary state sequence. Returns an infinite iterator; note, an iterator is preferable to a
   * stream because streams store all previously computed values in memory.
   *
   * @return An infinite iterator over states.
   */
  def generate(): Iterator[T] = {
    // Append elements until the state is the correct length.
    val state = ListBuffer[T]()
    (0 until this.order).foreach(i => state.append(predict(state.toList)))

    // Generate an infinite iterator over states.
    Iterator.continually({
      val next = predict(state.toList)
      state.remove(0)
      state.append(next)
      next
    })
  }

}
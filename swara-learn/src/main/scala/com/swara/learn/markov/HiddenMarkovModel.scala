package com.swara.learn.markov

import com.swara.learn.common.Distribution
import com.swara.learn.{Model, Supervised}
import net.jcip.annotations.NotThreadSafe
import scala.collection.mutable
import scala.collection.concurrent.TrieMap

/**
 * A hidden markov model (HMM) consists of observable states (O) and hidden states (H), in which the
 * hidden states have the Markov Property but cannot be directly observed. HMMs are widely used for
 * tasks such as speech, handwriting, and gesture recognition. The model is governed by three
 * probability distributions: initial (initial hidden states), transitions (between hidden states),
 * and emissions (observed state outputs). A hidden markov model may not be simultaneously trained
 * and used, but each operation may be performed separately in parallel.
 *
 * @tparam O Type of observed states.
 * @tparam H Type of hidden states.
 */
@NotThreadSafe
class HiddenMarkovModel[O, H] extends Model[Seq[O], Seq[H]] with Supervised[Seq[O], Seq[H]] {

  private[this] val transitions = TrieMap.empty[H, Distribution[H]]
  private[this] val emissions = TrieMap.empty[H, Distribution[O]]
  private[this] val initial = Distribution.empty[H]

  /**
   * Trains the hidden markov model on sequences of observed states and their associated hidden
   * states. Training is thread-safe; a hidden markov model may be concurrently trained on multiple
   * state sequences.
   *
   * @param observed Sequence of observations.
   * @param hidden Sequence of hidden states.
   */
  override def train(observed: Seq[O], hidden: Seq[H]): Unit = {
    require(hidden.nonEmpty)
    require(hidden.size == observed.size)

    // Record the initial hidden state.
    this.initial += hidden.head

    // Slide a window across the hidden states and record state transitions.
    hidden.iterator.sliding(2, 1).foreach { transition =>
      this.transitions.getOrElseUpdate(transition.head, Distribution.empty[H]) += transition.last
    }

    // Record emissions of observed states from hidden states.
    hidden.zip(observed).foreach { case (state, emission) =>
      this.emissions.getOrElseUpdate(state, Distribution.empty[O]) += emission
    }
  }

  /**
   * Returns the most likely sequence of hidden states for a given sequence of observed states using
   * an algorithm similar to A* and the variation of the Viterbi Algorithm described in detail at
   * http://people.csail.mit.edu/jonfeld/pubs/lazyviterbi.pdf.
   *
   * @param observed Observed state sequence.
   * @throws NoSuchElementException If no such sequence of hidden states exists.
   * @return Most likely hidden state sequence that generated it.
   */
  override def predict(observed: Seq[O]): Seq[H] = {
    if (observed.isEmpty) return Seq.empty

    // A step is a hidden state at a specified time, and a path is a probabilistic sequence of
    // steps. The goal of the algorithm is to determine the path with the maximum probability.
    // Construct a max-heap keyed by probability and a set of visited steps.
    case class Step(state: H, time: Int)
    case class Path(steps: Seq[Step], prob: Double)

    val maxheap = mutable.PriorityQueue.empty[Path](Ordering.by(_.prob))
    val visited = mutable.Set.empty[Step]

    // Enqueue all the initial paths with non-zero probabilities onto the max-heap.
    this.initial.foreach { state =>
      val emission = this.emissions.getOrElse(state, Distribution.empty)
      val prob = this.initial.probability(state) * emission.probability(observed.head)
      if (prob > 0) maxheap.enqueue(Path(List(Step(state, 0)), prob))
    }

    // Continually dequeue the maximum probability path until one of the specified length is found.
    // If the last step has not already been visited, then enqueue all non-zero next-paths onto
    // the max-heap. If a step has already been visited, then this implies that a more optimal
    // path to the step exists; therefore, we only consider paths with an unvisited last step.
    var max: Path = maxheap.dequeue()
    while (max.steps.length < observed.length) {
      val last: Step = max.steps.last
      val next: Int  = last.time + 1

      if (!visited.contains(last)) {
        val transition = this.transitions.getOrElse(last.state, Distribution.empty)
        val emission = this.emissions.getOrElse(last.state, Distribution.empty)

        transition.foreach { state =>
          val prob = max.prob * transition.probability(state) * emission.probability(observed(next))
          if (prob > 0) maxheap.enqueue(Path(max.steps :+ Step(state, next), prob))
        }
      }

      max = maxheap.dequeue()
    }

    // Unroll the maximum probability path into a sequence of hidden states.
    max.steps.map(_.state)
  }

  /**
   * Generates an iterator over an infinite sequence of observed states. Each iteration, the model
   * randomly emits an observed state and randomly transitions to a new hidden state. Note, an
   * iterator is preferable to a stream because streams cache previous computations.
   *
   * @throws NoSuchElementException If any of the distributions are empty.
   * @return An infinite iterator over observed states.
   */
  def generate(): Iterator[O] = {
    var state = this.initial.sample()
    Iterator.continually {
      val next = this.emissions(state).sample()
      state = this.transitions(state).sample()
      next
    }
  }

}

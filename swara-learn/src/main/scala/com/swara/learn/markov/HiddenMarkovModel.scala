package com.swara.learn.markov

import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import com.swara.learn.common.Distribution
import com.swara.learn.common.distributions.MarginalDistribution
import com.swara.learn.{Model, Supervised}
import com.swara.learn.markov.HiddenMarkovModel._
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
  * A hidden markov model (HMM) consists of observable states (O) and hidden states (H), in which the
  * hidden states have the Markov Property but cannot be directly observed. HMMs are widely used for
  * tasks such as speech, handwriting, and gesture recognition. The model is governed by three
  * probability distributions: initial (initial hidden states), transitions (between hidden states),
  * and emissions (observed state outputs). However, unlike most open source HMM implementations,
  * these probability distributions are implicitly defined and we rely on the famous [[Multiset]] to
  * determine probabilities only when they are needed.
  *
  * @tparam O Type of observed states.
  * @tparam H Type of hidden states.
  */
class HiddenMarkovModel[O, H] extends Model[Seq[O], Seq[H]] with Supervised[Seq[O], Seq[H]] {

  private[this] val initial     = Distribution.empty[H]
  private[this] val transitions = mutable.Map.empty[H, Distribution[H]]
  private[this] val emissions   = mutable.Map.empty[H, Distribution[O]]

  /**
    * Trains the hidden markov model on sequences of observed states and their associated hidden
    * states. Training is thread-safe.
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
    * Predicts the most likely sequence of hidden states for a given sequence of observed states
    * using the a variation of the Viterbi Algorithm similar to the algorithm described in
    * http://people.csail.mit.edu/jonfeld/pubs/lazyviterbi.pdf and to the A* algorithm. The algorithm
    * relies on the fundamental observation that optimal paths are themselves composed of optimal
    * paths (e.g. if a-b-c-d is an optimal path, then a-b and a-b-c are also optimal paths).
    *
    * @param observed Observed state sequence.
    * @return Most likely hidden state sequence that generated it.
    */
  override def predict(observed: Seq[O]): Seq[H] = {
    case class Step(state: H, time: Int)
    case class Path(steps: List[Step], prob: Double)

    // Construct a max-heap of paths keyed by their probability and a set of visited steps.
    val maxheap = mutable.PriorityQueue.empty[Path](Ordering.by(_.prob))
    val visited = mutable.Set.empty[Step]

    // Add all the initial paths to the max-heap that have non-zero probabilities.
    this.initial.foreach { state =>
      val emission = this.emissions.getOrElse(state, Distribution.empty)
      maxheap.enqueue(Path(
        List(Step(state, 0)),
        this.initial.probability(state) * emission.probability(observed.head)
      ))
    }

    // Continually dequeue the maximum element, until a path of the desired length is located.
    var max = maxheap.dequeue()
    while (max.steps.length < observed.length) {
      if (!visited.contains(max.steps.last)) {
        // Determine the transition and emission distributions.
        val transition = this.transitions.getOrElse(max.steps.last.state, Distribution.empty)
        val emission = this.emissions.getOrElse(max.steps.last.state, Distribution.empty)

        // Expand all possible next paths.
        val time = max.steps.length + 1
        transition.foreach { state =>
          maxheap.enqueue(Path(
            max.steps :+ Step(state, time),
            max.prob * transition.probability(state) * emission.probability(observed(time))
          ))
        }
      }

      max = maxheap.dequeue()
    }

    max.steps.map(_.state)
  }

  /**
    * Generates an iterator over an infinite sequence of observed states. Each iteration, the model
    * randomly emits an observed state and randomly transitions to a new hidden state. Note, an
    * iterator is preferable to a stream because streams cache previous computations.
    *
    * @return An infinite iterator over observed states.
    */
  def generate(): Iterator[O] = {
    var state: H = this.initial.sample()
    Iterator.continually({
      val next = this.emissions(state).sample()
      state = this.transitions(state).sample()
      next
    })
  }

}

object HiddenMarkovModel {

  /**
    * Returns a random element from a [[Multiset]] based on the frequency with which it appears.
    * Convenience method to facilitate the logic in the generation function. This method is not
    * thread-safe; the underlying [[Multiset]] may not change during this operation.
    *
    * @param set Multiset to randomly select from.
    * @return Randomly selected element in the set.
    */
  private def rand[T](set: Multiset[T]): T = {
    var num = Random.nextInt(set.size())
    set.iterator.asScala.dropWhile { element =>
      num -= set.count(element)
      num > 0
    }.next()
  }

}

package com.swara.learn.markov

import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import com.swara.learn.common.Distribution
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

  private[this] val initial = ConcurrentHashMultiset.create[H]()
  private[this] val transitions = new Distribution[H]
  private[this] val emissions = new Distribution[Any]

  /**
    * Trains the hidden markov model on sequences of observed states and their associated hidden
    * states. Training is thread-safe.
    *
    * @param examples Sequence of states to train on.
    */
  override def train(examples: Seq[(O, H)]): Unit = {
    if (examples.nonEmpty) {
      // Record the initial hidden state.
      this.initial.insert(examples.head._2)

      // Slide a window across the states and record transitions between hidden states.
      examples.iterator.map(_._2).sliding(2, 1).foreach { hidden =>
        this.transitions.insert(hidden(2), given = hidden(1))
      }

      // Iterate across the states and record emissions of observed states from hidden states.
      examples.iterator.foreach {
        case (observed, hidden) =>
          this.emissions.insert(observed, given = hidden)
      }
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
    case class Path(steps: List[Step], prob: Double) {
      def length: Int = steps.last.time
    }

    // Construct a max-heap of paths keyed by their probability and a set of visited steps.
    val maxheap = mutable.PriorityQueue.empty[Path](Ordering.by(_.prob))
    val visited = mutable.Set.empty[Step]

    // Add all the initial paths to the max-heap that have non-zero probabilities.
    this.initial.probabilities().foreach {
      case (state, iprob) =>
        maxheap.enqueue(
            Path(
                List(Step(state, 0)),
                iprob * this.emissions.probability(observed.head,
                                                   given = state)
            ))
    }

    var current = maxheap.dequeue()
    while (current.length < observed.length) {
      // If the last step in the current path is unvisited, then expand all possible neighbors.
      if (!visited.contains(current.steps.last)) {
        this.transitions
          .probabilities(given = current.steps.last.state)
          .foreach {
            case (state, tprob) =>
              maxheap.enqueue(
                  Path(
                      current.steps :+ Step(state, current.length + 1),
                      current.prob * tprob * this.emissions.probability(
                          observed(current.length + 1),
                          given = state)
                  ))
          }
      }

      // Dequeue elements from the max-heap until a path of the desired length is found.
      current = maxheap.dequeue()
    }

    current.steps.map(_.state)
  }

  /**
    * Generates an iterator over an infinite sequence of observed states. Each iteration, the model
    * randomly emits an observed state and randomly transitions to a new hidden state. Note, an
    * iterator is preferable to a stream because streams cache previous computations.
    *
    * @return An infinite iterator over observed states.
    */
  def generate(): Iterator[O] = {
    var state: H = rand(this.initial)
    Iterator.continually({
      val next = rand(this.emissions(state))
      state = rand(this.transitions(state))
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
    set.iterator.asScala
      .dropWhile(element => {
        num -= set.count(element)
        num > 0
      })
      .next()
  }

}

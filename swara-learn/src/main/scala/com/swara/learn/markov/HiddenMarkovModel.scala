package com.swara.learn.markov

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random
import java.util.concurrent.ConcurrentHashMap
import com.google.common.collect.{ConcurrentHashMultiset, Multiset}
import com.swara.learn.markov.HiddenMarkovModel._

/**
 * A hidden markov model (HMM) consists of observable states (O) and hidden states (H), in which the
 * hidden states have the Markov Property but cannot be directly observed. HMMs are widely used for
 * tasks such as speech, handwriting, and gesture recognition. The model is governed by three
 * probability distributions: initial (initial hidden states), transitions (between hidden states),
 * and emissions (observed state outputs). However, unlike most open source HMM implementations,
 * these probability distributions are implicitly defined and we rely on the famous [[Multiset]] to
 * determine probabilities only when they are needed.
 *
 * @tparam O Type of observed states
 * @tparam H Type of hidden states
 */
class HiddenMarkovModel[O, H] {

  private[this] val initial     = ConcurrentHashMultiset.create[H]()
  private[this] val transitions = new ConcurrentHashMap[H, Multiset[H]]().asScala
  private[this] val emissions   = new ConcurrentHashMap[H, Multiset[O]]().asScala

  /**
   * Trains the hidden markov model on sequences of observed states and their associated hidden
   * states. Training is thread-safe.
   *
   * @param states Sequence of states to train on
   */
  def train(states: Seq[(O, H)]): Unit = {
    if (states.nonEmpty) {
      // Record the initial hidden state.
      this.initial.add(states.head._2)

      // Slide a window across the states and record transitions between hidden states.
      states.iterator.map(_._2).sliding(2, 1).foreach { hidden => this.transitions
        .getOrElseUpdate(hidden(1), { ConcurrentHashMultiset.create[H]() })
        .add(hidden(2))
      }

      // Iterate across the states and record emissions of observed states from hidden states.
      states.iterator.foreach { case (observed, hidden) => this.emissions
        .getOrElseUpdate(hidden, { ConcurrentHashMultiset.create[O]() })
        .add(observed)
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
   * @param observed Observed state sequence
   * @return Most likely hidden state sequence that generated it
   */
  def predict(observed: Seq[O]): Seq[H] = {
    // Nodes contain a time and a hidden state, while paths form a linked list of nodes with an
    // associated length (aka probability). The goal of this algorithm, like the Viterbi Algorithm,
    // is to locate the maximum length path (aka most likely sequence) of nodes in time.
    case class Node(time: Int, state: H)
    case class Path(prev: Option[Node], prob: Double)

    // The algorithm maintains two data structures a max-heap (priority queue) and an implicit
    // trellis. The trellis stores processed nodes and the max-heap stores pending nodes keyed by
    // the length of their associated paths. The trellis and heap maintain two important invariants
    // that are central to the correctness of this algorithm: (1) the trellis contains the optimal
    // path for any processed node and (2) the prev pointer of any pending node in the max-heap does
    // not point to any other pending node.
    val maxheap = mutable.PriorityQueue.newBuilder[(Node, Path)](Ordering.by(_._2.prob))
    val trellis = mutable.HashMap[Node, Path]()

    // Add the initial states to the priority queue. Initial states are hidden states with a
    // non-zero probability of beginning a state sequence. The initial path probabilities are simply
    // the probability of the initial state multiplied by the probability of emitting the first
    // observed state from it.
    this.initial.asScala.foreach(state => maxheap.enqueue((
      Node(0, state),
      Path(None, prob(Some(this.initial), state) * prob(this.emissions.get(state), observed.head)))
    ))

    // Iteratively remove and process pending nodes from the max-heap until a node has a time equal
    // to the length of the observed state sequence. From (1) the node is guaranteed to form the
    // minimum length path and from (2) the previous nodes in the path are already processed.
    var next = maxheap.dequeue()
    while (next._1.time < observed.size - 1) {
      // If a pending node has already been processed, then from (1) we know the pending node forms
      // a suboptimal path. Otherwise, enqueue all possible next nodes for the pending node and add
      // it to the trellis. Then, dequeue the next node for processing.
      trellis.getOrElseUpdate(next._1, {
        this.transitions.get(next._1.state).foreach(_.asScala.foreach(state => maxheap.enqueue((
          Node(next._1.time + 1, state),
          Path(Some(next._1), next._2.prob *
            prob(this.transitions.get(next._1.state), state) *
            prob(this.emissions.get(state), observed(next._1.time + 1))))
        )))

        next._2
      })

      next = maxheap.dequeue()
    }

    // Unroll the minimum length path by recursing on the prev pointers. O(|observed|)
    def unroll(node: Node): List[H] = trellis(node) match {
      case Path(None, _) => node.state :: Nil
      case Path(Some(prev), _) => unroll(prev) :+ node.state
    }

    unroll(next._1)
  }

  /**
   * Generates an iterator over an infinite sequence of observed states. Each iteration, the model
   * randomly emits an observed state and randomly transitions to a new hidden state. Note, an
   * iterator is preferable to a stream because streams cache previous computations.
   *
   * @return An infinite iterator over observed states
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

  implicit val random: Random = Random

  def apply[O, H]() = new HiddenMarkovModel[O, H]

  /**
   * Returns the probability of an element based on the frequency with which it occurs in a
   * [[Multiset]]. Convenience method to facilitate the logic in the prediction and generation
   * functions. This method is not thread-safe; while the count and size methods are individually
   * atomic, the [[Multiset]] does not (and can not) make any guarantees that it won't change
   * between operations. However, this potential race-condition is unavoidable without a significant
   * performance penalty, and, therefore, we ignore it.
   */
  private def prob[T](set: Option[Multiset[T]], element: T): Double =
    set.fold(0.0)(s => s.count(element) / s.size.toDouble)

  /**
   * Returns a random element from a [[Multiset]] based on the frequency with which it appears.
   * Convenience method to facilitate the logic in the generation function. This method is not
   * thread-safe; the underlying [[Multiset]] may not change during this operation.
   */
  private def rand[T](set: Multiset[T])(implicit random: Random): T = {
    var num = random.nextInt(set.size())
    set.iterator.asScala.dropWhile(element => {
      num -= set.count(element)
      num > 0
    }).next()
  }

}
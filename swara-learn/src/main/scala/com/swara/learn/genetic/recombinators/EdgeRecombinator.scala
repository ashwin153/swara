package com.swara.learn.genetic.recombinators

import com.swara.learn.genetic.Recombinator
import scala.collection.mutable
import scala.util.Random

/**
 * An edge recombinator. Edge recombination is typically used to crossover genomes with
 * non-repeating gene sequences such as paths in the travelling salesman problem. Edge recombination
 * strives to produce offspring with the fewest modifications to the parent paths as possible.
 *
 * @tparam T Type of genome.
 */
class EdgeRecombinator[T] extends Recombinator[Seq[T]] {

  override def crossover(father: Seq[T], mother: Seq[T]): Seq[T] = {
    // Build an adjacency matrix connecting all genes to all neighbor genes.
    var adjacency = new mutable.HashMap[T, Set[T]] with mutable.MultiMap[T, T]
    father.sliding(2).foreach(e => adjacency.addBinding(e(0), e(1)))
    mother.sliding(2).foreach(e => adjacency.addBinding(e(0), e(1)))
    adjacency.foreach { case (k, v) => v.foreach(adjacency.addBinding(_, k)) }

    // If the last gene currently exists, then remove it from all entries in the adjacency matrix.
    // If the last gene does not exist or it has no neighbors, then choose a random gene that is not
    // currently in the genome. Otherwise, choose the neighbor gene with the fewest neighbors.
    var child = mutable.ListBuffer.empty[T]
    while (child.length < father.length) {
      child += (child.lastOption flatMap adjacency.get match {
        case Some(neighbors) if neighbors.nonEmpty =>
          neighbors.foreach(adjacency.removeBinding(_, child.last))
          neighbors.min(Ordering.by(adjacency.getOrElse(_, Set.empty).size))
        case _ => Random.shuffle(adjacency.keys.filterNot(child.contains)).head
      })
    }

    child
  }

}
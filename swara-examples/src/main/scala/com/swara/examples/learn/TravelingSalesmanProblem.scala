package com.swara.examples.learn

import scala.util.Random
import com.swara.learn.common._
import com.swara.learn.genetic.recombinators.EdgeRecombinator
import com.swara.learn.genetic.selectors.RouletteWheelSelector
import com.swara.learn.genetic.{Evaluator, Mutator, Population}

/**
 * The traveling salesman problem (TSP) asks the following questions: "Given a list of cities and
 * the distances between each pair of cities, what is the shortest possible route that visits each
 * city exactly once and returns to the origin city?" (Wikipedia). The TSP is NP-complete; however,
 * we will attempt to use Genetic Algorithms to solve it.
 */
object TravelingSalesmanProblem {

  def apply(distance: Array[Array[Double]]): Seq[Int] = {
    // The fitness of a route is equal to the negative sum of the distances between cities, because
    // the goal of the problem is to find shortest possible route that visits all cities.
    object RouteEvaluator extends Evaluator[Seq[Int]] {
      override def fitness(genome: Seq[Int]): Double =
        (genome :+ genome.head).sliding(2).foldLeft(0.0)((t, c) => t - distance(c(0))(c(1)))
    }

    // Randomly swaps the positions of two cities in the route.
    class RouteMutator(rate: Double) extends Mutator[Seq[Int]] {
      override def mutate(genome: Seq[Int]): Seq[Int] = {
        val i = Random.nextInt(genome.size)
        val j = Random.nextInt(genome.size)
        if (Random.nextDouble() < this.rate) genome.swap(i, j) else genome
      }
    }

    // Evolve a population of 100 random permutations of the cities for 50 generations, and then
    // return the member of the population with the maximum fitness (shortest distance).
    var population = Population(Seq.fill(100)(distance.indices.shuffle))

    (1 to 50).foreach { _ =>
      population = population.evolve(
        new RouletteWheelSelector,
        new EdgeRecombinator,
        new RouteMutator(0.1),
        RouteEvaluator,
        elitism = 0.05
      )
    }

    population.members.maxBy(RouteEvaluator.fitness)
  }

}

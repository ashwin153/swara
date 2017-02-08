package com.swara.learn.examples

import breeze.linalg._
import breeze.stats.distributions.Rand
import com.swara.learn.common._
import com.swara.learn.genetic.recombinators.EdgeRecombinator
import com.swara.learn.genetic.selectors.{RouletteWheelSelector, TournamentSelector}
import com.swara.learn.genetic.{Evaluator, Population}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PopulationTest extends FunSuite {

  test("Function Maximization") {
    val f = (x: Double) => -x * x
    var population = Population(Seq.fill(20)(Rand.uniform.map(_ * 10.0 - 20.0).draw))

    // Evolve the population for a few generations, and print the optimal individual.
    (1 to 20).foreach { gen =>
      population = population.evolve(
        new TournamentSelector(5),
        (f, m) => (f + m) / 2.0,
        x => x + Rand.gaussian.draw(),
        x => f(x),
        elitism = 0.05
      )

      println(gen + ": " + population.members.maxBy(f))
    }

  }

  test("Travelling Salesman Problem") {
    // Number of cities.
    val n = 25

    // The distance between city i and city j must: (1) be non-negative, (2) be zero for i = j, and
    // (3) be equal to the distance between city j and city i. Therefore, we construct the distance
    // matrix as a random, symmetric matrix with a zero diagonal.
    val rand = Matrix.rand(n, n, Rand.uniform)
    val dist = Matrix.tabulate(n, n)((i, j) => if (i == j) 0.0 else rand(min(i, j), max(i, j)))

    // According to Wikipedia, the travelling salesman problem asks the following question: "Given a
    // list of cities and the distances between each pair of cities, what is the shortest possible
    // route that visits each city exactly once and returns to the origin city?" Therefore, our
    // "chromosomes" consist of ordered permutations of city indexes whose "fitness" is equal to the
    // sum of the distances between all cities.
    object TravellingSalesmanEvaluator extends Evaluator[Seq[Int]] {
      override def fitness(genome: Seq[Int]): Double =
        (genome :+ genome.head).sliding(2).foldLeft(0.0)((t, c) => t + dist(c(0), c(1)))
    }

    // Generate random permutations of the specified sequence using the Donald Knuth Shuffle.
    var population = Population(Seq.fill(100)((0 until n).shuffle))

    // Evolve the population for a few generations, and return the optimal individual.
    (1 to 50).foreach { gen =>
      population = population.evolve(
        new RouletteWheelSelector,
        new EdgeRecombinator,
        x => x,
        TravellingSalesmanEvaluator,
        elitism = 0.05
      )

      println(gen + ": " + population.members.maxBy(TravellingSalesmanEvaluator.fitness))
    }

  }

}
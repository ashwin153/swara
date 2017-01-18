package com.swara.learn.genetic

import breeze.linalg._
import breeze.stats.distributions.Rand
import org.scalatest.FunSuite
import com.swara.learn.genetic.selectors.RouletteWheelSelector
import com.swara.learn.genetic.recombinators.EdgeRecombinator
import com.swara.learn.genetic.mutators.SwapMutator
import scala.util.Random

class PopulationTest extends FunSuite {

  test("Travelling Salesman Problem") {
    // Number of cities.
    val n = 10

    // The distance between city i and city j must: (1) be non-negative, (2) be zero for i = j, and
    // (3) be equal to the distance between city j and city i. Therefore, we construct the distance
    // matrix as a random, symmetric matrix with a zero diagonal.
    val rand = Matrix.rand(n, n, Rand.uniform)
    val dist = Matrix.tabulate[Double](n, n) { (i, j) =>
      if (i == j) 0.0 else rand(min(i, j), max(i, j))
    }

    println(dist)
//    // According to Wikipedia, the travelling salesman problem asks the following question: "Given a
//    // list of cities and the distances between each pair of cities, what is the shortest possible
//    // route that visits each city exactly once and returns to the origin city?" Therefore, our
//    // "chromosomes" consist of ordered permutations of city indexes whose "fitness" is equal to the
//    // sum of the distances between all cities.
//    object TravellingSalesmanEvaluator extends Evaluator[Seq[Int]] {
//      override def fitness(genome: Seq[Int]): Double =
//        (genome :+ genome.head).sliding(2).foldLeft(0.0)((t, c) => t + dist(c(0), c(1)))
//    }
//
//    // Construct an initial population consisting of random permutations of sequences of cities.
//    val init = Random.shuffle((0 until n).permutations).take(n^2).toSeq
//    var population = Population(init, TravellingSalesmanEvaluator)
//
//    // Evolve the population for
//    (1 to 50).foreach { gen =>
//      population = population.evolve(
//        new RouletteWheelSelector,
//        new EdgeRecombinator,
//        new SwapMutator(0.10),
//        TravellingSalesmanEvaluator,
//        elitism = 0.10
//      )
//    }
//
//    //
  }

}
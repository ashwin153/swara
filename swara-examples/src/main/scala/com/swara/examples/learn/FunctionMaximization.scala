package com.swara.examples.learn

import breeze.stats.distributions.Rand
import com.swara.learn.genetic.Population
import com.swara.learn.genetic.selectors.TournamentSelector

/**
 * Function maximization is the problem of finding the value of x such that some function f(x) is
 * maximized. Suppose f is not differentiable; then, traditional approaches, like finding points
 * where the derivative vanishes, are no longer possible. However, this more general problem may
 * still be solved by a Genetic Algorithm.
 */
object FunctionMaximization {

  def apply(func: Double => Double, from: Double, until: Double): Double = {
    val generator  = Rand.uniform.map(_ * (until - from) + from)
    var population = Population(Seq.fill(20)(generator.draw()))

    (1 to 20).foreach { _ =>
      population = population.evolve(
        new TournamentSelector(5),
        (f, m) => (f + m) / 2.0,
        x => x + Rand.gaussian.draw(),
        x => func(x),
        elitism = 0.05
      )
    }

    population.members.maxBy(func)
  }

}

package com.swara.learn.genetic

/**
 * A population. Populations are ordered collections of individuals sorted by descending fitness.
 *
 * @param individuals Members of the population.
 * @tparam T Type of genome.
 */
class Population[T] private (
  val individuals: Seq[Individual[T]]
) {

  /**
   * Evolves the population according to the rules of Darwinian natural selection. Individuals in
   * the population are selected for recombination by their evaluated fitness and their recombinated
   * offspring are mutated to produce the next generation of the population. Iterative application
   * of population evolution will lead to more optimal individuals in successive generations.
   *
   * @param selector Selects individuals for mating.
   * @param recombinator Recombines the genetic makeup of mates to produce offspring.
   * @param mutator Mutates the genetic makeup of individuals.
   * @param evaluator Evaluates the fitness of individuals.
   * @param elitism Percentage of individuals to keep intact in the next generation.
   * @return Next generation of the population.
   */
  def evolve(
    selector: Selector[T],
    recombinator: Recombinator[T],
    mutator: Mutator[T],
    evaluator: Evaluator[T],
    elitism: Double = 0.0
  ): Population[T] = {
    // Select the highest fitness individuals in the population and copy them unchanged into the
    // next generation. This procedure, known as elitism, can dramatically impact the performance of
    // genetic algorithms because it ensures that good candidate solutions do not have to be
    // constantly rediscovered. However, elitism can lead to a lack of diversity in the population
    // which in turn reduces the algorithm's ability to break out of local extrema.
    require(elitism >= 0.0 && elitism <= 1.0, "Elitism percentage must be on the interval [0, 1].")
    val elite = this.individuals.take((elitism * this.individuals.size).toInt)

    // Select individuals in the population to be ancestors. The genomes of these individuals are
    // recombinated to produce offspring, which are then mutated to foster diversity. Because these
    // operators are independent of each other, they may be performed in parallel.
    val ancestors = selector.select(this.individuals, 2 * (this.individuals.size - elite.size))
    val offspring = ancestors.par.iterator.grouped(2).map { case Seq(father, mother) =>
      val child = mutator.mutate(recombinator.crossover(father.genome, mother.genome))
      Individual(child, evaluator.fitness(child))
    }

    // Generate a new population from the unchanged elites and the mutated offspring.
    Population[T](elite ++ offspring)
  }

}

object Population {

  /**
   * Constructs a population containing the specified sequence of individuals.
   *
   * @param individuals Members of population.
   * @tparam T Type of genome.
   * @return Population containing the specified individuals.
   */
  def apply[T](individuals: Seq[Individual[T]]): Population[T] =
    new Population[T](individuals.sortBy(-_.fitness))

  def apply[T](genomes: Seq[T], evaluator: Evaluator[T]): Population[T] =
    Population(genomes.map(i => Individual(i, evaluator.fitness(i))))
}
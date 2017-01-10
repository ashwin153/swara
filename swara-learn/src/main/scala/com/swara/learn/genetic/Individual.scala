package com.swara.learn.genetic

import net.jcip.annotations.Immutable

/**
 * An individual organism. Individuals are members of a [[Population]] that have some genetic makeup
 * and a fitness score. The genetic makeups of individuals in a population are evolved over time
 * according to the rules of Darwinian natural selection; individuals with higher fitness scores are
 * more likely to survive and pass on their genome to future generations.
 *
 * @param genome Genetic material of an individual.
 * @param fitness Fitness of individual.
 * @tparam T Type of genome.
 */
@Immutable
case class Individual[T](genome: T, fitness: Double)
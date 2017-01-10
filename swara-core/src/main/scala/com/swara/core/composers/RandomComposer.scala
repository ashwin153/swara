package com.swara.core.composers

import com.swara.core.Composer
import com.swara.music.elements.{Chord, Voice}

import scala.util.Random
import collection.JavaConversions._

/**
 * A randomized composer. Random composers should produce chord sequences that are far less
 * musically correct and interesting than other methods; therefore, they can be used as a benchmark
 * to ensure that other techniques are working properly.
 */
class RandomComposer extends Composer {

  override def compose(examples: Seq[Voice]): Iterator[Chord] = {
    // Aggregate all the various chords in all the example voices.
    val chords = examples.flatMap(_.chords.toIndexedSeq)

    // Generate a stream of random chords sampled from the aggregated list.
    Iterator.continually(chords(Random.nextInt(chords.size)))
  }

}

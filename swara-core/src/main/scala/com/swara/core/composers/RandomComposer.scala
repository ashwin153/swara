package com.swara.core.composers

import com.swara.core.Composer
import com.swara.music.elements.{Chord, Voice}

import scala.util.Random
import collection.JavaConversions._

class RandomComposer extends Composer {

  override def compose(examples: Seq[Voice]): Iterator[Chord] = {
    // Aggregate all the various chords in all the example voices.
    val chords = examples.flatMap(_.chords)

    // Generate a stream of random chords sampled from the aggregated list.
    Iterator.continually(chords(Random.nextInt(chords.size)))
  }

}

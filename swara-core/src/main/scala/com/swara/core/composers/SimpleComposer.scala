package com.swara.core.composers

import com.swara.core.Composer
import com.swara.learn.markov.DiscreteMarkovChain
import com.swara.music.elements.{Chord, Note, Voice}
import org.apache.commons.math3.fraction.Fraction
import scala.collection.JavaConverters._

class SimpleComposer extends Composer {

  override def compose(examples: Seq[Voice]): Iterator[Chord] = {
    // Create and train three markov chains that separately generate rhythm, dynamics, and melody.
    // Because the markov chain implementation is thread-safe they can be trained concurrently on
    // all the sample songs in parallel.
    val rhythm   = new DiscreteMarkovChain[Fraction](5)
    val dynamics = new DiscreteMarkovChain[Int](2)
    val melody   = new DiscreteMarkovChain[Set[Note]](2)

    examples.par.foreach { voice =>
      val chords = voice.chords().asScala
      rhythm.train(chords.map(_.duration()))
      dynamics.train(chords.map(_.volume()))
      melody.train(chords.map(_.notes().asScala.toSet))
    }

    // Generate an iterator over chords by merging the output of the three markov chains.
    val durations = rhythm.generate()
    val volumes   = dynamics.generate()
    val notes     = melody.generate()

    Iterator.continually(new Chord.Builder()
      .withNotes(notes.next().asJava)
      .withDuration(durations.next())
      .withVolume(volumes.next())
      .build()
    )
  }

}

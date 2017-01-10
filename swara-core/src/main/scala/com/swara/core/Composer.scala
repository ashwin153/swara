package com.swara.core

import com.swara.music.elements.{Chord, Voice}

/**
 * A music composer. Composers generate original sequences of chords that are representative of but
 * distinct from a set of example voices. Composers may utilize a variety of machine learning and
 * statistical techniques to produce musically correct and interesting chord sequences.
 */
trait Composer {

  def compose(examples: Seq[Voice]): Iterator[Chord]

}

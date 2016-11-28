package com.swara.core

import com.swara.music.elements.{Chord, Voice}

trait Composer {

  /**
   * Returns an infinite iterator over chords that are generated from the specified sequence of
   * example voices.
   *
   * @param examples
   * @return
   */
  def compose(examples: Seq[Voice]): Iterator[Chord]

}

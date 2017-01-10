package com.swara.core

import com.swara.music.elements.Song

/**
 * A song synthesizer. While a [[Composer]] is responsible for generating sequences of chords, a
 * synthesizer is responsible for synthesizing a song. Synthesizers are the main entry point into
 * the library.
 */
trait Synthesizer {

  def synthesize(dataset: Iterable[Song]): Song

}

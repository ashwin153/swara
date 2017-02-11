package com.swara.examples.music

import com.swara.music._
import scala.util.Try

object PlaybackExample {

  def apply(song: Song): Try[Unit] = play(song)

}

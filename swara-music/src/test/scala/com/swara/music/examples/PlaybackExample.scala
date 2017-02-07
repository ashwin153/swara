package com.swara.music.examples

import com.swara.music.readers.MidiReader
import com.swara.music._
import java.io.FileInputStream
import org.scalatest.FunSuite

class PlaybackExample extends FunSuite {

  test("Playback midi file.") {
    val in = new FileInputStream("swara-music/src/test/resources/test.mid")
    MidiReader.read(in).map(play)
    in.close()
  }

}

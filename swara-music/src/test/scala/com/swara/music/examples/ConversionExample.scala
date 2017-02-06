package com.swara.music.examples

import com.swara.music._
import com.swara.music.readers._
import com.swara.music.writers._
import java.io.File
import org.scalatest.FunSuite

class ConversionExample extends FunSuite {

  test("Convert midi to midi.") {
    convert(MidiReader, MidiWriter)(
      new File("swara-music/src/test/resources/test.mid"),
      new File("swara-music/src/test/resources/midi2midi.mid")
    )
  }

  test("Convert midi to json.") {
    convert(MidiReader, JsonWriter)(
      new File("swara-music/src/test/resources/test.mid"),
      new File("swara-music/src/test/resources/midi2json.json")
    )
  }

  test("Convert json to midi.") {
    convert(JsonReader, MidiWriter)(
      new File("swara-music/src/test/resources/test.json"),
      new File("swara-music/src/test/resources/json2midi.mid")
    )
  }

}

package com.swara.examples.music

import com.swara.music._
import com.swara.music.readers._
import com.swara.music.writers._
import java.io.File
import scala.util.Try

object ConversionExample {

  def apply(in: File, out: File): Try[Unit] = {
    val file = """([^.]*)\.([^.]*)""".r

    val reader = in.getName match {
      case file(_, ext) if ext == "mid"  => MidiReader
      case file(_, ext) if ext == "json" => JsonReader
      case file(_, ext) if ext == "ser"  => JavaReader
      case _ => throw new UnsupportedOperationException
    }

    val writer = in.getName match {
      case file(_, ext) if ext == "mid"  => MidiWriter
      case file(_, ext) if ext == "json" => JsonWriter
      case file(_, ext) if ext == "ser"  => JavaWriter
      case _ => throw new UnsupportedOperationException
    }

    convert(reader, writer)(in, out)
  }

}

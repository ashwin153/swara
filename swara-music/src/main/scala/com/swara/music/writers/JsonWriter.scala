package com.swara.music
package writers

import com.fasterxml.jackson.databind.SerializationFeature
import java.io.OutputStream
import scala.util.Try
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization

/**
 * Writes a [[Song]] to a json string. Json conversion is particularly important to facilitate
 * interoperability; without a convenient format for transferring musical information between
 * programming languages, the complex logic in [[com.swara.music.readers.MidiReader]] and in the
 * [[MidiWriter]] would have to be constantly rewritten in every supported language. Now, only the
 * json parsing and core classes would need to be replicated.
 */
object JsonWriter extends MusicWriter {

  implicit val formats = DefaultFormats

  mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false)

  override def write(song: Song, out: OutputStream): Try[Unit] =
    Try(Serialization.write(song, out))

}

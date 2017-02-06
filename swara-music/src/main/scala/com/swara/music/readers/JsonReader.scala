package com.swara.music
package readers

import com.fasterxml.jackson.databind.SerializationFeature
import java.io.InputStream
import scala.util.Try
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Reads a [[Song]] from a json string. Json conversion is particularly important to facilitate
 * interoperability; without a convenient format for transferring musical information between
 * programming languages, the complex logic in the [[com.swara.music.writers.MidiWriter]] and in the
 * [[MidiReader]] would have to be constantly rewritten in every supported language.
 */
object JsonReader extends MusicReader {

  implicit val formats = DefaultFormats

  mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false)

  override def read(in: InputStream): Try[Song] = Try(parse(in).extract[Song])

}

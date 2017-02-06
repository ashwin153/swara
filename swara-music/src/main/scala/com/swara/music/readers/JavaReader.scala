package com.swara.music
package readers

import java.io.{InputStream, ObjectInputStream}
import scala.util.Try

/**
 * Reads a [[Song]] from a stream encoded using the standard Java Serialization Api. This reader is
 * included as a way of benchmarking the performance of alternative deserialization techniques.
 * Because each [[MusicElement]] is necessarily [[Serializable]], the JavaReader is guaranteed to
 * work on all data structures.
 */
object JavaReader extends MusicReader {

  override def read(in: InputStream): Try[Song] =
    Try(new ObjectInputStream(in).readObject().asInstanceOf[Song])

}

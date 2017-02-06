package com.swara.music.writers

import com.swara.music.{MusicElement, MusicWriter, Song}
import java.io.{ObjectOutputStream, OutputStream}
import scala.util.Try

/**
 * Writes a [[Song]] to a stream encoded using the standard Java Serialization API. This writer
 * is included as a way of benchmarking the performance of alternative techniques. Because each
 * [[MusicElement]] is necessarily [[Serializable]], the JavaReader is guaranteed to work on all
 * data structures.
 */
object JavaWriter extends MusicWriter {

  override def write(song: Song, out: OutputStream): Try[Unit] =
    Try(new ObjectOutputStream(out).writeObject(song))

}
